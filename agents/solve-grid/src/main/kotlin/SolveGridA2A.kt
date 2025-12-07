package fr.nicolaslinard.koog.kmp.agents.solvegrid

import A2AHandler
import A2ARouter
import A2ATaskRequest
import A2ATaskResult
import A2AStatus
import GridTools

/**
 * Grid solver A2A handlers. Default demo action: AoC 2024 Day 4 Part 1 — count occurrences of the word "XMAS".
 */
object SolveGridA2A {
    fun register() {
        val cap = "aoc.solve.grid"
        A2ARouter.register(cap, "xmasCount", xmasCount())
    }

    private fun xmasCount(): A2AHandler = { req: A2ATaskRequest ->
        val grid = req.payload ?: ""
        try {
            val tools = GridTools()
            val value = tools.countWordOccurrences(grid, "XMAS")
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"grid_xmas\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }
}
