package fr.nicolaslinard.koog.kmp.agents.solvearith

import A2AHandler
import A2ARouter
import A2ATaskRequest
import A2ATaskResult
import A2AStatus

object SolveArithA2A {
    fun register() {
        val cap = "aoc.solve.arith"
        A2ARouter.register(cap, "calibrationSum", calibrationSumHandler())
        A2ARouter.register(cap, "dialSimulation", dialSimulationHandler())
        A2ARouter.register(cap, "dialSimulationPart2", dialSimulationPart2Handler())
    }

    private fun calibrationSumHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveCalibrationSum(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"calibrationSum\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun dialSimulationHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveDialSimulation(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"dialSimulation\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun dialSimulationPart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveDialSimulationPart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"dialSimulationPart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }
}
