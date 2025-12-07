import java.io.File

fun solveTachyonManifold(input: String): Int {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    if (lines.isEmpty()) return 0

    // Find start position 'S'
    var startCol = -1
    for (i in lines[0].indices) {
        if (lines[0][i] == 'S') {
            startCol = i
            break
        }
    }
    if (startCol == -1) return 0

    var splitCount = 0
    var activeBeams = mutableSetOf(startCol)

    // Process each row from top to bottom
    for (row in 1 until lines.size) {
        val nextBeams = mutableSetOf<Int>()

        for (col in activeBeams) {
            if (col >= 0 && col < lines[row].length) {
                if (lines[row][col] == '^') {
                    // Beam hits a splitter - it splits
                    splitCount++
                    println("Row $row, Col $col: Split #$splitCount")
                    // Emit new beams from immediate left and right
                    if (col - 1 >= 0) nextBeams.add(col - 1)
                    if (col + 1 < lines[row].length) nextBeams.add(col + 1)
                } else {
                    // Beam continues downward
                    nextBeams.add(col)
                }
            }
        }

        activeBeams = nextBeams
        println("After row $row: active beams at columns $activeBeams")
        if (activeBeams.isEmpty()) break
    }

    return splitCount
}

fun main() {
    val example = File("test_day7_example.txt").readText()
    val result = solveTachyonManifold(example)
    println("\nFinal result: $result (expected: 21)")
}
