import java.util.UUID

// Very small in‑process A2A envelope and router to standardize inter‑agent exchanges for Jalon B.

data class A2ATaskRequest(
    val capability: String,
    val action: String,
    val payload: String? = null,
    val correlationId: String = UUID.randomUUID().toString(),
    val parentId: String? = null
)

data class A2ATaskResult(
    val correlationId: String,
    val status: A2AStatus,
    val payload: String? = null,
    val error: String? = null
)

enum class A2AStatus { OK, ERROR }

typealias A2AHandler = (A2ATaskRequest) -> A2ATaskResult

object A2ARouter {
    private val handlers: MutableMap<String, MutableMap<String, A2AHandler>> = mutableMapOf()

    @Synchronized
    fun register(capability: String, action: String, handler: A2AHandler) {
        val actMap = handlers.getOrPut(capability) { mutableMapOf() }
        actMap[action] = handler
    }

    fun send(req: A2ATaskRequest): A2ATaskResult {
        val actMap = handlers[req.capability]
        val h = actMap?.get(req.action)
        return if (h != null) {
            try {
                h(req)
            } catch (t: Throwable) {
                A2ATaskResult(
                    correlationId = req.correlationId,
                    status = A2AStatus.ERROR,
                    payload = null,
                    error = t.message ?: t.toString()
                )
            }
        } else {
            A2ATaskResult(
                correlationId = req.correlationId,
                status = A2AStatus.ERROR,
                payload = null,
                error = "No handler for ${req.capability}/${req.action}"
            )
        }
    }
}

// Centralized registrar to ensure all modules register their handlers when orchestration starts.
// Registration is performed by the Orchestrator module to avoid utils → agents circular dependencies.
