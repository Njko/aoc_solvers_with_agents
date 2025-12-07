package fr.nicolaslinard.koog.kmp.agents.intent

import A2AHandler
import A2ARouter
import A2ATaskRequest
import A2ATaskResult
import A2AStatus

object IntentA2A {
    fun register() {
        val capability = "aoc.intent"
        A2ARouter.register(capability, "parse", handler())
    }

    private fun handler(): A2AHandler = { req: A2ATaskRequest ->
        val input = req.payload ?: ""
        try {
            val tools = IntentTools()
            val json = tools.parseIntent(input)
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }
}
