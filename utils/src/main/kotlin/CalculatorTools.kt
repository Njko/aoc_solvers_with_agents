import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Tools for performing basic arithmetic operations")
class CalculatorTools : ToolSet {

    @Tool
    @LLMDescription("Add two numbers and return their sum")
    fun add(
        @LLMDescription("First number to add (integer value")
        a: Int,
        @LLMDescription("Second number to add (integer value")
        b: Int,
    ): String {
        val sum = a + b
        return "The sum of $a and $b is $sum"
    }
}