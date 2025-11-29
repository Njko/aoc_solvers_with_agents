import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@LLMDescription("Tools to fetch Advent of Code inputs and read local files. Avoids logging sensitive data.")
class AoCInputTools : ToolSet {

    private val client: HttpClient = HttpClient.newHttpClient()

    @Tool
    @LLMDescription("Fetches the Advent of Code input for a given year/day using the AOC session token. The session is resolved from env var 'AOC_SESSION', JVM prop 'AOC_SESSION', or file '~/.aoc/session'. Returns the raw puzzle input as text.")
    fun fetchInput(year: Int, day: Int): String {
        val session = resolveSession() ?: error("AOC session cookie not found. Set env var AOC_SESSION or create ~/.aoc/session.")
        val url = "https://adventofcode.com/$year/day/$day/input"
        val req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Cookie", "session=$session")
            .header("User-Agent", "KoogMultiagentProject/1.0 (github:none)")
            .GET()
            .build()
        val res = client.send(req, HttpResponse.BodyHandlers.ofString())
        if (res.statusCode() !in 200..299) error("AoC fetch failed: HTTP ${res.statusCode()}")
        return normalizeNewlines(res.body())
    }

    @Tool
    @LLMDescription("Reads a local text file and returns its content as a string (UTF-8). Useful fallback for offline runs.")
    fun readLocal(path: String): String = normalizeNewlines(File(path).readText(Charsets.UTF_8))

    @Tool
    @LLMDescription("Normalizes newlines to \n and trims trailing newlines. Returns normalized text.")
    fun normalizeNewlines(text: String): String =
        text.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r')

    private fun resolveSession(): String? {
        // Priority: JVM property -> ENV var -> file
        val fromProp = System.getProperty("AOC_SESSION")?.ifBlank { null }
        if (fromProp != null) return fromProp
        val fromEnv = System.getenv("AOC_SESSION")?.ifBlank { null }
        if (fromEnv != null) return fromEnv
        val home = System.getProperty("user.home") ?: return null
        val file = File(home, ".aoc/session")
        if (file.exists()) return file.readText(Charsets.UTF_8).trim().ifBlank { null }
        // Fallback constant embedded in project (as requested). Never log this value.
        return Secrets.AOC_SESSION_FALLBACK.ifBlank { null }
    }
}
