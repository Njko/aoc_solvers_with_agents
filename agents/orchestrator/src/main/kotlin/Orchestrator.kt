package fr.nicolaslinard.koog.kmp.agents.orchestrator

import MathTools
import CalculatorTools
import AoCInputTools
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.llm.OllamaModels
import fr.nicolaslinard.koog.kmp.agents.intent.IntentTools
import fr.nicolaslinard.koog.kmp.agents.solvearith.SolveArithTools

object OrchestratorFactory {

    fun build(): AIAgent<String, String> {
        val executor = simpleOllamaAIExecutor()

        val toolRegistry = ToolRegistry {
            tools(IntentTools())
            tools(AoCInputTools())
            tools(SolveArithTools())
            tools(MathTools())
            tools(CalculatorTools())
        }

        val systemPrompt = """
            Tu es un orchestrateur Advent of Code.
            Consignes incontournables:
            - Comprends des requêtes en français (ex: "Résouds le jour 1 de 2023", "Résouds l'année 2024").
            - Utilise STRICTEMENT les outils fournis. Ne calcule pas dans ta tête si un outil est disponible.
            - Pour ce prototype (Jalon A), seul le puzzle de type "calibration sum" (similaire à AoC 2023 Day 1 Part 1) est supporté.
            - Si l'utilisateur demande un autre jour/année/part, réponds poliment que seul 2023 Day 1 Part 1 est disponible pour l'instant.
            - Processus si jour 1 de 2023:
              1) Appelle l'outil parseIntentFr(text) pour obtenir year/day/part.
              2) Si year=2023 et day=1: appelle fetchInput(year, day) pour obtenir l'input.
              3) Appelle solveCalibrationSum(input) pour calculer la réponse.
              4) Réponds uniquement avec la valeur numérique comme résultat principal et une courte explication en français.
            - NE JAMAIS divulguer ni journaliser des secrets (cookies, tokens). N'affiche jamais le cookie AoC.
            - Les échanges entre outils et toi sont considérés sûrs; ne répète pas les entrées brutes si elles sont très longues.
        """.trimIndent()

        return AIAgent(
            promptExecutor = executor,
            llmModel = OllamaModels.Meta.LLAMA_3_2,
            systemPrompt = systemPrompt,
            toolRegistry = toolRegistry,
            strategy = functionalStrategy { input ->
                var responses = requestLLMMultiple(input)
                while (responses.containsToolCalls()) {
                    val calls = extractToolCalls(responses)
                    val results = executeMultipleTools(calls)
                    responses = sendMultipleToolResults(results)
                }
                responses.single().asAssistantMessage().content
            },
            maxIterations = 30
        )
    }
}
