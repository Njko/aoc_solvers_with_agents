package fr.nicolaslinard.koog.kmp.agents.io

import A2AHandler
import A2ARouter
import A2AStatus
import A2ATaskResult
import AoCInputTools

object IoA2A {
    fun register() {
        val cap = "aoc.input"
        A2ARouter.register(cap, "fetch", fetch())
        A2ARouter.register(cap, "readLocal", readLocal())
    }

    private fun fetch(): A2AHandler = { req ->
        val text = req.payload ?: ""
        try {
            val year = Regex("\"year\"\\s*:\\s*(\\d{4})").find(text)?.groupValues?.get(1)?.toInt()
            val day = Regex("\"day\"\\s*:\\s*(\\d{1,2})").find(text)?.groupValues?.get(1)?.toInt()
            require(year != null && day != null) { "Invalid payload for fetch: expected {\"year\":YYYY,\"day\":D}" }
            val tools = AoCInputTools()
            val raw = tools.fetchInput(year, day)
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = raw)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun readLocal(): A2AHandler = { req ->
        val text = req.payload ?: ""
        try {
            val path = Regex("\"path\"\\s*:\\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1)
            require(!path.isNullOrBlank()) { "Invalid payload for readLocal: expected {\"path\":\"...\"}" }
            val tools = AoCInputTools()
            val raw = tools.readLocal(path)
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = raw)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }
}
