import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Verification helpers for validating solver outputs against expected samples")
class VerifyTools : ToolSet {

    @Tool
    @LLMDescription("Compares two integers and returns a simple 'OK' or 'KO: expected X got Y' string.")
    fun compareInt(expected: Int, actual: Int): String =
        if (expected == actual) "OK" else "KO: expected ${expected} got ${actual}"
}
