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
        A2ARouter.register(cap, "verticalMathWorksheet", verticalMathWorksheetHandler())
        A2ARouter.register(cap, "verticalMathWorksheetPart2", verticalMathWorksheetPart2Handler())
        A2ARouter.register(cap, "tachyonManifold", tachyonManifoldHandler())
        A2ARouter.register(cap, "tachyonManifoldPart2", tachyonManifoldPart2Handler())
        A2ARouter.register(cap, "giftShop", giftShopHandler())
        A2ARouter.register(cap, "giftShopPart2", giftShopPart2Handler())
        A2ARouter.register(cap, "batteryJoltage", batteryJoltageHandler())
        A2ARouter.register(cap, "batteryJoltagePart2", batteryJoltagePart2Handler())
        A2ARouter.register(cap, "forkliftAccess", forkliftAccessHandler())
        A2ARouter.register(cap, "forkliftAccessPart2", forkliftAccessPart2Handler())
        A2ARouter.register(cap, "playgroundJunctionBoxes", playgroundJunctionBoxesHandler())
        A2ARouter.register(cap, "playgroundJunctionBoxesPart2", playgroundJunctionBoxesPart2Handler())
        A2ARouter.register(cap, "movieTheater", movieTheaterHandler())
        A2ARouter.register(cap, "movieTheaterPart2", movieTheaterPart2Handler())
        A2ARouter.register(cap, "factoryLights", factoryLightsHandler())
        A2ARouter.register(cap, "factoryJoltage", factoryJoltageHandler())
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

    private fun verticalMathWorksheetHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveVerticalMathWorksheet(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"verticalMathWorksheet\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun verticalMathWorksheetPart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveVerticalMathWorksheetPart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"verticalMathWorksheetPart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun tachyonManifoldHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveTachyonManifold(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"tachyonManifold\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun tachyonManifoldPart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveTachyonManifoldPart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"tachyonManifoldPart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun giftShopHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveGiftShopPart1(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"giftShop\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun giftShopPart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveGiftShopPart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"giftShopPart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun batteryJoltageHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveBatteryJoltagePart1(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"batteryJoltage\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun batteryJoltagePart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveBatteryJoltagePart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"batteryJoltagePart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun forkliftAccessHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveForkliftAccessPart1(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"forkliftAccess\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun forkliftAccessPart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveForkliftAccessPart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"forkliftAccessPart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun playgroundJunctionBoxesHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solvePlaygroundJunctionBoxes(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"playgroundJunctionBoxes\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun playgroundJunctionBoxesPart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solvePlaygroundJunctionBoxesPart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"playgroundJunctionBoxesPart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun movieTheaterHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveMovieTheater(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"movieTheater\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun movieTheaterPart2Handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveMovieTheaterPart2(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"movieTheaterPart2\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun factoryLightsHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveFactoryLights(input)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"factoryLights\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun factoryJoltageHandler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = SolveArithTools()
            val value = tools.solveFactoryJoltage(input)
            val json = "{" +
                    "\"part\":2," +
                    "\"value\":$value," +
                    "\"method\":\"factoryJoltage\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }
}
