import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Grid utilities for AoC puzzles: parse and scan words in 8 directions")
class GridTools : ToolSet {

    @Tool
    @LLMDescription("Counts occurrences of a word in a rectangular grid (provided as multi-line text), scanning 8 directions (N, NE, E, SE, S, SW, W, NW). Case-sensitive.")
    fun countWordOccurrences(gridText: String, word: String): Int {
        if (word.isEmpty()) return 0
        val lines = gridText.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r').split('\n')
        if (lines.isEmpty()) return 0
        val h = lines.size
        val w = lines[0].length
        val grid = Array(h) { y -> lines[y].toCharArray() }
        val dirs = arrayOf(
            intArrayOf(0, -1),  // N
            intArrayOf(1, -1),  // NE
            intArrayOf(1, 0),   // E
            intArrayOf(1, 1),   // SE
            intArrayOf(0, 1),   // S
            intArrayOf(-1, 1),  // SW
            intArrayOf(-1, 0),  // W
            intArrayOf(-1, -1)  // NW
        )
        var count = 0
        for (y in 0 until h) {
            for (x in 0 until w) {
                if (grid[y][x] != word[0]) continue
                for (d in dirs) {
                    if (matchWord(grid, w, h, x, y, d[0], d[1], word)) count++
                }
            }
        }
        return count
    }

    private fun matchWord(grid: Array<CharArray>, w: Int, h: Int, sx: Int, sy: Int, dx: Int, dy: Int, word: String): Boolean {
        var x = sx
        var y = sy
        for (i in word.indices) {
            if (x !in 0 until w || y !in 0 until h) return false
            if (grid[y][x] != word[i]) return false
            x += dx
            y += dy
        }
        return true
    }
}
