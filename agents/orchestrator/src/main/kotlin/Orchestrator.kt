package fr.nicolaslinard.koog.kmp.agents.orchestrator

import A2ARouter
import A2ATaskRequest
import AoCInputTools
import BenchTools
import CalculatorTools
import MathTools
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels
import fr.nicolaslinard.koog.kmp.agents.intent.IntentA2A
import fr.nicolaslinard.koog.kmp.agents.intent.IntentTools
import fr.nicolaslinard.koog.kmp.agents.io.IoA2A
import fr.nicolaslinard.koog.kmp.agents.solvearith.SolveArithA2A
import fr.nicolaslinard.koog.kmp.agents.solvearith.SolveArithTools
import fr.nicolaslinard.koog.kmp.agents.solvegraph.SolveGraphA2A
import fr.nicolaslinard.koog.kmp.agents.solvegrid.SolveGridA2A
import fr.nicolaslinard.koog.kmp.agents.verify.VerifyA2A

object OrchestratorFactory {

    enum class Protocol { A2A, JSON }

    fun build(protocol: Protocol = Protocol.A2A): AIAgent<String, String> {
        val executor = simpleOllamaAIExecutor()

        val toolRegistry = ToolRegistry {
            tools(IntentTools())
            tools(AoCInputTools())
            tools(SolveArithTools())
            tools(MathTools())
            tools(CalculatorTools())
        }

        val systemPrompt = """
            You are an Advent of Code orchestrator.
            Essential instructions:
            - Understand requests in plain English (e.g., "solve day 1 of 2023", "solve day 4 of 2024").
            - Use STRICTLY the provided tools. Do not calculate in your head if a tool is available.
            - Supported puzzles: 2023 Day 1 (calibration sum), 2024 Day 4 (XMAS grid), 2021 Day 12 (cave paths), 2025 Days 1-4, 6-8 (various puzzles).
            - If the user requests an unsupported day/year/part, politely respond that only the listed puzzles are currently available.
            - Process:
              1) Call parseIntent(text) to get year/day/part.
              2) Fetch the input using the appropriate tool.
              3) Route to the correct solver based on year and day.
              4) Respond only with the numeric value as the main result and a brief explanation.
            - NEVER disclose or log secrets (cookies, tokens). Never display the AoC cookie.
            - Exchanges between tools and you are considered safe; do not repeat raw inputs if they are very long.
        """.trimIndent()

        // Register A2A handlers (idempotent) when A2A is selected
        if (protocol == Protocol.A2A) {
            try { IntentA2A.register() } catch (_: Throwable) {}
            try { IoA2A.register() } catch (_: Throwable) {}
            try { SolveArithA2A.register() } catch (_: Throwable) {}
            try { SolveGridA2A.register() } catch (_: Throwable) {}
            try { SolveGraphA2A.register() } catch (_: Throwable) {}
            try { VerifyA2A.register() } catch (_: Throwable) {}
        }

        return AIAgent(
            promptExecutor = executor,
            llmModel = OllamaModels.Meta.LLAMA_3_2,
            systemPrompt = systemPrompt,
            toolRegistry = toolRegistry,
            strategy = functionalStrategy { input ->
                val benchEnabled = (System.getProperty("AOC_BENCH") == "1") || (System.getenv("AOC_BENCH") == "1")
                if (benchEnabled) BenchTools.reset()
                if (protocol == Protocol.A2A) {
                    // 1) Intent via A2A
                    if (benchEnabled) BenchTools.start("intent")
                    val intentRes = A2ARouter.send(
                        A2ATaskRequest(capability = "aoc.intent", action = "parse", payload = input)
                    )
                    if (benchEnabled) BenchTools.stop("intent")
                    val intentJson = intentRes.payload ?: return@functionalStrategy "Error in intent parsing: ${intentRes.error ?: "unknown"}"
                    val year = Regex("\"year\"\\s*:\\s*(\\d{4})").find(intentJson)?.groupValues?.get(1)?.toIntOrNull()
                    val day = Regex("\"day\"\\s*:\\s*(\\d{1,2})").find(intentJson)?.groupValues?.get(1)?.toIntOrNull()
                    val part = Regex("\"part\"\\s*:\\s*(\\d)").find(intentJson)?.groupValues?.get(1)?.toIntOrNull()
                    if (year == null || day == null) {
                        return@functionalStrategy "Incomplete request: please specify a request like 'solve day 1 of 2023'."
                    }

                    // 2) Fetch input via A2A
                    val fetchPayload = "{" + "\"year\":$year,\"day\":$day" + "}"
                    if (benchEnabled) BenchTools.start("io")
                    val inputRes = A2ARouter.send(
                        A2ATaskRequest(capability = "aoc.input", action = "fetch", payload = fetchPayload)
                    )
                    if (benchEnabled) BenchTools.stop("io")
                    val raw: String = inputRes.payload ?: return@functionalStrategy "Error fetching input: ${inputRes.error ?: "unknown"}"

                    // 3) Route to solver using declarative routing DSL
                    val route = SolverRegistry.findRoute(year, day)
                    val out = if (route != null) {
                        val executor = RouteExecutor(benchEnabled, if (benchEnabled) BenchTools else null)
                        val result = executor.execute(route, part, raw)
                        result.fold(
                            onSuccess = { it },
                            onFailure = { error ->
                                val errorMsg: String = error.message ?: "Unknown error"
                                return@functionalStrategy errorMsg
                            }
                        )
                    } else {
                        SolverRegistry.supportedPuzzles()
                    }
                    if (benchEnabled) {
                        println(BenchTools.report())
                    }
                    return@functionalStrategy out
                } else {
                    // JSON/legacy mode: uses declarative routing DSL
                    val intent = parseIntentLocal(input)
                        ?: return@functionalStrategy "Limited functionality: please specify a request like: solve day 1 of 2023."

                    val route = SolverRegistry.findRoute(intent.year, intent.day)
                    val result = route?.let {
                        val io = AoCInputTools()
                        // JSON mode currently supports arithmetic solvers only
                        val solverTools = mapOf(
                            "aoc.solve.arith" to SolveArithTools()
                        )
                        val executor = JsonModeExecutor(
                            inputFetcher = { year, day -> io.fetchInput(year, day) },
                            solverTools = solverTools
                        )
                        executor.execute(it, intent.part, intent.year, intent.day)
                    } ?: Result.failure(Exception(SolverRegistry.supportedPuzzles()))

                    return@functionalStrategy result.getOrElse { error ->
                        error.message ?: "Unknown error in JSON mode"
                    }
                }
            },
            maxIterations = 30,
            temperature = 0.1
        )
    }

    private data class LocalIntent(val year: Int, val day: Int, val part: Int?)

    private fun parseIntentLocal(text: String): LocalIntent? {
        val t = text.trim().lowercase()
        // Match: "day <D> of <YYYY>" with optional "part <P>"
        val reDayYearPart = Regex(".*day\\s+(\\d{1,2}).*of\\s*(\\d{4})(?:.*part\\s*(\\d))?.*")
        val m = reDayYearPart.matchEntire(t) ?: return null
        val day = m.groupValues.getOrNull(1)?.toIntOrNull() ?: return null
        val year = m.groupValues.getOrNull(2)?.toIntOrNull() ?: return null
        val part = m.groupValues.getOrNull(3)?.toIntOrNull()
        return LocalIntent(year, day, part)
    }
}
