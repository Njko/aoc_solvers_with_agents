import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Basic text parsing helpers for AoC inputs")
class TextParsingTools : ToolSet {

    @Tool
    @LLMDescription("Splits the given text into lines, ignoring empty trailing line")
    fun lines(text: String): List<String> =
        text.split('\n').let { if (it.isNotEmpty() && it.last().isEmpty()) it.dropLast(1) else it }

    @Tool
    @LLMDescription("Extracts all decimal digits from a line and returns them as a list of Int (each 0..9)")
    fun digits(line: String): List<Int> = line.filter { it.isDigit() }.map { it.digitToInt() }

    @Tool
    @LLMDescription("For a line containing digits, returns the two-digit number formed by the first and last digit. If only one digit exists, it duplicates it (e.g., '7' -> 77). Returns -1 if no digit.")
    fun firstLastNumber(line: String): Int {
        val ds = digits(line)
        if (ds.isEmpty()) return -1
        val first = ds.first()
        val last = ds.last()
        return first * 10 + last
    }
}
