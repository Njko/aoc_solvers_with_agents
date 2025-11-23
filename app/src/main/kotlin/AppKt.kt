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
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main() {
    //preflightChecks()

    //continueAvecExempleBasicAgent()
    //continueAvecExempleFunctionalAgent()
    continueAvecExepleComplexe()
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
