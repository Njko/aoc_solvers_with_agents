package fr.nicolaslinard.koog.kmp.agents.verify

import A2AHandler
import A2ARouter
import A2ATaskRequest
import A2ATaskResult
import A2AStatus
import VerifyTools

object VerifyA2A {
    fun register() {
        val cap = "aoc.verify"
        A2ARouter.register(cap, "compareInt", compareInt())
    }

    private fun compareInt(): A2AHandler = { req ->
        val text = req.payload ?: ""
        try {
            val expected = Regex("\\\"expected\\\"\\s*:\\s*(\\\\d+)").find(text)?.groupValues?.get(1)?.toInt()
            val actual = Regex("\\\"actual\\\"\\s*:\\s*(\\\\d+)").find(text)?.groupValues?.get(1)?.toInt()
            require(expected != null && actual != null) { "Invalid payload for compareInt: expected {\"expected\":N,\"actual\":M}" }
            val tools = VerifyTools()
            val res = tools.compareInt(expected, actual)
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = "{\"result\":\"$res\"}")
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }
}
