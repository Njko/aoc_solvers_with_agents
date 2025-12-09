package fr.nicolaslinard.koog.kmp.agents.orchestrator

import A2ARouter
import A2ATaskRequest

/**
 * DSL for defining AoC solver routes in a declarative, maintainable way.
 * Follows object calisthenics principles and eliminates code duplication.
 */

@DslMarker
annotation class RouteDsl

data class PuzzleKey(val year: Int, val day: Int)

sealed class SolverAction {
    data class SingleAction(val name: String) : SolverAction()
    data class PartBasedAction(val part1: String, val part2: String) : SolverAction()

    // Naming convention: A2A action "dialSimulation" → method "solveDialSimulation"
    fun toMethodName(part: Int? = null): String {
        return when (this) {
            is SingleAction -> "solve${name.replaceFirstChar { it.uppercase() }}"
            is PartBasedAction -> {
                val actionName = if (part == 2) part2 else part1
                "solve${actionName.replaceFirstChar { it.uppercase() }}"
            }
        }
    }

    fun toA2aAction(part: Int? = null): String {
        return when (this) {
            is SingleAction -> name
            is PartBasedAction -> if (part == 2) part2 else part1
        }
    }
}

data class RouteConfig(
    val capability: String,
    val action: SolverAction,
    val errorName: String
)

class RouteBuilder {
    var capability: String = ""
    private var part1Action: String? = null
    private var part2Action: String? = null
    private var singleAction: String? = null
    var errorName: String = ""

    fun part1(action: () -> String) {
        part1Action = action()
    }

    fun part2(action: () -> String) {
        part2Action = action()
    }

    fun action(name: () -> String) {
        singleAction = name()
    }

    fun build(): RouteConfig {
        val solverAction = when {
            singleAction != null -> SolverAction.SingleAction(singleAction!!)
            part1Action != null && part2Action != null -> SolverAction.PartBasedAction(part1Action!!, part2Action!!)
            else -> throw IllegalStateException("Must specify either action() or both part1() and part2()")
        }

        require(capability.isNotEmpty()) { "capability must be set" }
        require(errorName.isNotEmpty()) { "errorName must be set" }

        return RouteConfig(capability, solverAction, errorName)
    }
}

@RouteDsl
class SolverRoutesBuilder {
    private val routes = mutableMapOf<PuzzleKey, RouteConfig>()

    fun route(year: Int, day: Int, configure: RouteBuilder.() -> Unit) {
        val builder = RouteBuilder()
        builder.configure()
        routes[PuzzleKey(year, day)] = builder.build()
    }

    fun build(): Map<PuzzleKey, RouteConfig> = routes.toMap()
}

fun solverRoutes(configure: SolverRoutesBuilder.() -> Unit): Map<PuzzleKey, RouteConfig> {
    val builder = SolverRoutesBuilder()
    builder.configure()
    return builder.build()
}

/**
 * Executes a route by sending an A2A request with proper benchmarking.
 */
class RouteExecutor(
    private val benchEnabled: Boolean,
    private val benchTools: Any? = null
) {
    private val valueExtractor = Regex("\"value\"\\s*:\\s*(\\d+)")

    fun execute(
        route: RouteConfig,
        part: Int?,
        input: String
    ): Result<String> {
        val request = A2ATaskRequest(
            capability = route.capability,
            action = route.action.toA2aAction(part),
            payload = input
        )

        return runWithBenchmark("solve") {
            val response = A2ARouter.send(request)

            response.payload?.let { payload ->
                extractValue(payload)
            } ?: Result.failure(
                Exception("Error in ${route.errorName} solver: ${response.error ?: "unknown"}")
            )
        }
    }

    private fun extractValue(payload: String): Result<String> {
        val value = valueExtractor.find(payload)?.groupValues?.get(1)
        return value?.let { Result.success(it) }
            ?: Result.success(payload)
    }

    private fun <T> runWithBenchmark(name: String, block: () -> T): T {
        if (benchEnabled) {
            try {
                benchTools?.javaClass?.getMethod("start", String::class.java)
                    ?.invoke(benchTools, name)
            } catch (_: Exception) { }
        }

        val result = block()

        if (benchEnabled) {
            try {
                benchTools?.javaClass?.getMethod("stop", String::class.java)
                    ?.invoke(benchTools, name)
            } catch (_: Exception) { }
        }

        return result
    }
}

/**
 * JSON mode executor - directly calls solver tools without A2A
 */
class JsonModeExecutor(
    private val inputFetcher: (year: Int, day: Int) -> String,
    private val solverTools: Map<String, Any>
) {
    fun execute(route: RouteConfig, part: Int?, year: Int, day: Int): Result<String> {
        return try {
            val input = inputFetcher(year, day)
            val methodName = route.action.toMethodName(part)
            val solver = solverTools[route.capability]
                ?: return Result.failure(Exception("No solver found for capability: ${route.capability}"))

            val method = solver::class.java.methods.find { it.name == methodName }
                ?: return Result.failure(Exception("Method $methodName not found on ${solver::class.java.simpleName}"))

            val result = method.invoke(solver, input)
            Result.success(result.toString())
        } catch (e: Exception) {
            Result.failure(Exception("Error executing ${route.errorName}: ${e.message}"))
        }
    }
}

/**
 * Registry holding all configured solver routes.
 */
object SolverRegistry {
    val routes: Map<PuzzleKey, RouteConfig> = solverRoutes {
        route(year = 2025, day = 1) {
            capability = "aoc.solve.arith"
            part1 { "dialSimulation" }
            part2 { "dialSimulationPart2" }
            errorName = "dial simulation"
        }

        route(year = 2025, day = 2) {
            capability = "aoc.solve.arith"
            part1 { "giftShop" }
            part2 { "giftShopPart2" }
            errorName = "gift shop"
        }

        route(year = 2025, day = 3) {
            capability = "aoc.solve.arith"
            part1 { "batteryJoltage" }
            part2 { "batteryJoltagePart2" }
            errorName = "battery joltage"
        }

        route(year = 2025, day = 4) {
            capability = "aoc.solve.arith"
            part1 { "forkliftAccess" }
            part2 { "forkliftAccessPart2" }
            errorName = "forklift access"
        }

        route(year = 2025, day = 6) {
            capability = "aoc.solve.arith"
            part1 { "verticalMathWorksheet" }
            part2 { "verticalMathWorksheetPart2" }
            errorName = "vertical math worksheet"
        }

        route(year = 2025, day = 7) {
            capability = "aoc.solve.arith"
            part1 { "tachyonManifold" }
            part2 { "tachyonManifoldPart2" }
            errorName = "tachyon manifold"
        }

        route(year = 2025, day = 8) {
            capability = "aoc.solve.arith"
            part1 { "playgroundJunctionBoxes" }
            part2 { "playgroundJunctionBoxesPart2" }
            errorName = "playground"
        }

        route(year = 2025, day = 9) {
            capability = "aoc.solve.arith"
            part1 { "movieTheater" }
            part2 { "movieTheaterPart2" }
            errorName = "movie theater"
        }

        route(year = 2024, day = 4) {
            capability = "aoc.solve.grid"
            action { "xmasCount" }
            errorName = "grid"
        }

        route(year = 2021, day = 12) {
            capability = "aoc.solve.graph"
            action { "cavePathsCount" }
            errorName = "graph"
        }

        route(year = 2023, day = 1) {
            capability = "aoc.solve.arith"
            action { "calibrationSum" }
            errorName = "arith"
        }
    }

    fun findRoute(year: Int, day: Int): RouteConfig? =
        routes[PuzzleKey(year, day)]

    fun supportedPuzzles(): String {
        val puzzles = routes.keys
            .groupBy { it.year }
            .entries
            .sortedByDescending { it.key }
            .joinToString(", ") { (year, days) ->
                val daysList = days.map { it.day }.sorted().joinToString(",")
                "$year/Days $daysList"
            }
        return "Not supported: current support includes $puzzles."
    }
}
