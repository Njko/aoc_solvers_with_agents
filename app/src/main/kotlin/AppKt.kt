package fr.nicolaslinard.koog.kmp.app

import MathTools
import CalculatorTools
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.asAssistantMessage
import ai.koog.agents.core.dsl.extension.containsToolCalls
import ai.koog.agents.core.dsl.extension.executeMultipleTools
import ai.koog.agents.core.dsl.extension.extractToolCalls
import ai.koog.agents.core.dsl.extension.nodeExecuteTool
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResult
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.dsl.extension.requestLLMMultiple
import ai.koog.agents.core.dsl.extension.sendMultipleToolResults
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels
import kotlinx.coroutines.runBlocking
import fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    // Démarrage par défaut: orchestrateur AoC
    val fromArgs = parseRequestFromArgs(args)
    val fromEnv = System.getenv("AOC_REQUEST")?.takeIf { it.isNotBlank() }
    val request = fromArgs ?: fromEnv
    val protocol = parseProtocolFromArgs(args)
    val bench = parseBenchFromArgs(args)
    if (bench) System.setProperty("AOC_BENCH", "1")
    runOrchestratorFlow(request, protocol)
    // Vous pouvez réactiver les exemples de base au besoin:
    // continueAvecExempleBasicAgent()
    // continueAvecExempleFunctionalAgent()
    // continueAvecExepleComplexe()
}

val promptExecutor = simpleOllamaAIExecutor()
fun continueAvecExepleComplexe() = runBlocking {
    val agent = AIAgent(
        promptExecutor = promptExecutor,
        toolRegistry = toolRegistry,
        agentConfig = agentConfigComplex,
        strategy = agentStrategy,
        installFeatures = {
            install(EventHandler) {
                onAgentStarting { eventContext ->
                    println("Starting agent : ${eventContext.agent.id}")
                }
                onAgentCompleted { eventContext ->
                    println("Result: ${eventContext.result}")
                }
            }
        }
    )

    println("Enter two numbers to add (e.g., 'add 5 and 7' or '5 + 7'):")

    // Read the user input and send it to the agent
    val userInput = readlnOrNull() ?: ""
    val agentResult = agent.run(userInput)
    println("The agent returned: $agentResult")
}

fun runOrchestratorFlow(requestOverride: String? = null, protocol: fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory.Protocol = fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory.Protocol.A2A) = runBlocking {
    println("Multi-agent Advent of Code Assistant (Milestone C - ${'$'}protocol)")
    ensureAoCSession()
    val finalRequest = requestOverride ?: run {
        println("Enter your request (e.g., 'solve day 1 of 2023'):")
        readlnOrNull()?.ifBlank { null }
    } ?: "solve day 1 of 2023"

    val agent = OrchestratorFactory.build(protocol)
    val result = agent.run(finalRequest)
    println("Result: $result")
}

private fun ensureAoCSession() {
    val hasProp = System.getProperty("AOC_SESSION")?.isNotBlank() == true
    val hasEnv = System.getenv("AOC_SESSION")?.isNotBlank() == true
    if (!hasProp && !hasEnv) {
        println("AOC_SESSION not defined. You can set it as an environment variable or paste it now (hidden input not supported).\nWARNING: the value will not be logged, but will remain visible in your console history.")
        print("Paste your AoC session cookie (or leave empty to skip): ")
        val cookie = readlnOrNull()?.trim()
        if (!cookie.isNullOrEmpty()) {
            // Don't display the value. Set it only as a JVM property for this run.
            System.setProperty("AOC_SESSION", cookie)
            println("AoC cookie registered in memory for this process.")
        } else {
            println("Continuing without cookie: only local file reading will work.")
        }
    }
}

private fun parseRequestFromArgs(args: Array<String>): String? {
    if (args.isEmpty()) return null
    // Very small CLI parser: supports --request "..." or --request=...
    val idx = args.indexOfFirst { it == "--request" || it.startsWith("--request=") }
    if (idx == -1) return null
    val token = args[idx]
    return if (token.startsWith("--request=")) token.substringAfter("--request=")
    else args.getOrNull(idx + 1)
}

private fun parseProtocolFromArgs(args: Array<String>): fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory.Protocol {
    if (args.isEmpty()) return fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory.Protocol.A2A
    val idx = args.indexOfFirst { it == "--protocol" || it.startsWith("--protocol=") }
    if (idx == -1) return fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory.Protocol.A2A
    val value = if (args[idx].startsWith("--protocol=")) args[idx].substringAfter("--protocol=") else args.getOrNull(idx + 1)
    return when (value?.lowercase()) {
        "json" -> fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory.Protocol.JSON
        else -> fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory.Protocol.A2A
    }
}

private fun parseBenchFromArgs(args: Array<String>): Boolean {
    if (args.isEmpty()) return false
    val idx = args.indexOfFirst { it == "--bench" || it.startsWith("--bench=") }
    if (idx == -1) return false
    val value = if (args[idx].startsWith("--bench=")) args[idx].substringAfter("--bench=") else null
    return when (value?.lowercase()) {
        null -> true
        "1", "true", "yes", "on" -> true
        "0", "false", "no", "off" -> false
        else -> true
    }
}

val toolRegistry = ToolRegistry {
    tools(MathTools())
    tools(CalculatorTools())
}

val agentStrategy = strategy("Simple calculator") {
    val nodeSendInput by nodeLLMRequest()
    val nodeExecuteTool by nodeExecuteTool()
    val nodeSendToolResult by nodeLLMSendToolResult()

    edge(nodeStart forwardTo nodeSendInput)
    edge((nodeSendInput forwardTo nodeExecuteTool) onToolCall { true })
    edge(nodeExecuteTool forwardTo nodeSendToolResult)
    edge((nodeSendToolResult forwardTo nodeFinish) transformed { it } onAssistantMessage { true })
}

val agentConfig = AIAgentConfig.withSystemPrompt(
    prompt = """
        You are a simple calculator assistant.
        You can add two numbers together using the calculator tool.
        When the user provides input, extract the numbers they want to add.
        The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
        Extract the two numbers and use the calculator tool to add them.
        Always respond with a clear, friendly message showing the calculation and result.
        """.trimIndent()
)

val agentConfigComplex = AIAgentConfig(
    prompt = Prompt.build("simple-calculator") {
        system("""
                You are a simple calculator assistant.
                You can add two numbers together using the calculator tool.
                When the user provides input, extract the numbers they want to add.
                The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
                Extract the two numbers and use the calculator tool to add them.
                Always respond with a clear, friendly message showing the calculation and result.
                """.trimIndent())
    },
    model = OllamaModels.Meta.LLAMA_3_2,
    maxAgentIterations = 10
)

fun continueAvecExempleFunctionalAgent() = runBlocking {
    val mathAgent = AIAgent<String, String>(
        promptExecutor = promptExecutor,
        llmModel = OllamaModels.Meta.LLAMA_3_2,
        systemPrompt = "You are a precise math assistant. When multiplication is needed, use the multiplication tool.",
        toolRegistry = toolRegistry,
        strategy = functionalStrategy { input ->
            var responses = requestLLMMultiple(input)

            while (responses.containsToolCalls()) {
                val pendingCalls = extractToolCalls(responses)
                val results = executeMultipleTools(pendingCalls)
                responses = sendMultipleToolResults(results)
            }
            responses.single().asAssistantMessage().content
        }
    )

    val result = mathAgent.run("Multiply 12.5 and 4, then add 10 to the result.")
    println("Reponse OLLAMA: $result")
}

private fun preflightChecks() {
    println("Koog + Ollama quick check")

    // Vérifie que le service Ollama est accessible en local (conforme à la doc Koog Getting Started / Ollama)
    val ollamaUrl = System.getenv("OLLAMA_HOST")?.ifBlank { null } ?: "http://localhost:11434"
    val client = HttpClient.newHttpClient()

    val req = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create("$ollamaUrl/api/tags"))
        .build()

    try {
        val res = client.send(req, HttpResponse.BodyHandlers.ofString())
        if (res.statusCode() in 200..299) {
            println("Ollama est accessible sur $ollamaUrl ✔")
            println("Modèles disponibles (extrait):")
            // On n'analyse pas le JSON en détail pour rester minimaliste
            println(res.body().take(500))
            println("\nVous pouvez maintenant suivre la doc Koog pour faire un appel via l'agent.")

        } else {
            println("Ollama a répondu avec le statut ${res.statusCode()} — vérifiez que le service est démarré et qu'un modèle est tiré (ex: 'ollama pull llama3.2').")
        }
    } catch (e: Exception) {
        println("Impossible de joindre Ollama sur $ollamaUrl. Démarrez-le puis réessayez. Détail: ${e.message}")
    }
}

fun continueAvecExempleBasicAgent() = runBlocking {
    val basicAgent = AIAgent(
        promptExecutor = simpleOllamaAIExecutor(),
        llmModel = OllamaModels.Meta.LLAMA_3_2,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
        temperature = 0.75,
        toolRegistry = ToolRegistry {
            tool(SayToUser)
        },
        maxIterations = 30
    )
    val result = basicAgent.run("Hello! How can you help me?")
    println("Reponse OLLAMA: $result")
}
