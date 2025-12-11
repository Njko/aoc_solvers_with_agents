package fr.nicolaslinard.koog.kmp.app

import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import kotlinx.coroutines.runBlocking
import fr.nicolaslinard.koog.kmp.agents.orchestrator.OrchestratorFactory

fun main(args: Array<String>) {
    // Démarrage par défaut: orchestrateur AoC
    val fromArgs = parseRequestFromArgs(args)
    val fromEnv = System.getenv("AOC_REQUEST")?.takeIf { it.isNotBlank() }
    val request = fromArgs ?: fromEnv
    val protocol = parseProtocolFromArgs(args)
    val bench = parseBenchFromArgs(args)
    if (bench) System.setProperty("AOC_BENCH", "1")
    runOrchestratorFlow(request, protocol)
}

val promptExecutor = simpleOllamaAIExecutor()

fun runOrchestratorFlow(requestOverride: String? = null, protocol: OrchestratorFactory.Protocol = OrchestratorFactory.Protocol.A2A) = runBlocking {
    println($$"Multi-agent Advent of Code Assistant (Milestone C - $protocol)")
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

    // Check for explicit --request flag
    val idx = args.indexOfFirst { it == "--request" || it.startsWith("--request=") }
    if (idx != -1) {
        val token = args[idx]
        return if (token.startsWith("--request=")) token.substringAfter("--request=")
        else args.getOrNull(idx + 1)
    }

    // If no --request flag, treat all args as the request (joined with spaces)
    // Filter out other flags like --protocol, --bench, etc.
    val requestArgs = args.filterNot {
        it.startsWith("--protocol") || it.startsWith("--bench") || it == "json" || it == "a2a"
    }

    return if (requestArgs.isNotEmpty()) requestArgs.joinToString(" ") else null
}

private fun parseProtocolFromArgs(args: Array<String>): OrchestratorFactory.Protocol {
    if (args.isEmpty()) return OrchestratorFactory.Protocol.A2A
    val idx = args.indexOfFirst { it == "--protocol" || it.startsWith("--protocol=") }
    if (idx == -1) return OrchestratorFactory.Protocol.A2A
    val value = if (args[idx].startsWith("--protocol=")) args[idx].substringAfter("--protocol=") else args.getOrNull(idx + 1)
    return when (value?.lowercase()) {
        "json" -> OrchestratorFactory.Protocol.JSON
        else -> OrchestratorFactory.Protocol.A2A
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
