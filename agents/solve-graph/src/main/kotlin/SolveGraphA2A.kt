package fr.nicolaslinard.koog.kmp.agents.solvegraph

import A2AHandler
import A2ARouter
import A2ATaskRequest
import A2ATaskResult
import A2AStatus
import GraphTools

/**
 * Graph solver A2A handlers. Demo: AoC 2021 Day 12 Part 1 — count paths in cave graph.
 */
object SolveGraphA2A {
    fun register() {
        val cap = "aoc.solve.graph"
        A2ARouter.register(cap, "cavePathsCount", cavePathsCount())
        A2ARouter.register(cap, "reactorPaths", reactorPaths())
        A2ARouter.register(cap, "reactorPathsPart2", reactorPathsPart2())
    }

    private fun cavePathsCount(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = GraphTools()
            val value = tools.countCavePathsPart1(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"graph_cave_paths\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun reactorPaths(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = GraphTools()
            val value = tools.countReactorPaths(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"reactor_paths\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun reactorPathsPart2(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = GraphTools()
            val value = tools.countReactorPathsWithRequiredNodes(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"reactor_paths_part2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }
}
