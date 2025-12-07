import java.util.concurrent.ConcurrentHashMap

/** Simple in-process benchmark helper for timing named sections. */
object BenchTools {
    private val starts = ConcurrentHashMap<String, Long>()
    private val durs = ConcurrentHashMap<String, Long>()

    fun reset() {
        starts.clear(); durs.clear()
    }

    fun start(section: String) {
        starts[section] = System.nanoTime()
    }

    fun stop(section: String) {
        val s = starts.remove(section)
        if (s != null) {
            val ns = System.nanoTime() - s
            durs[section] = (ns / 1_000_000) // store in ms
        }
    }

    fun durationMs(section: String): Long? = durs[section]

    fun report(): String {
        if (durs.isEmpty()) return ""
        return durs.entries.sortedBy { it.key }.joinToString(
            separator = ", ", prefix = "[bench] ", postfix = ""
        ) { (k, v) -> "$k=${v}ms" }
    }
}
