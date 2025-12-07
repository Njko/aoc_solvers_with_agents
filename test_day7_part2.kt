import java.io.File

fun solveTachyonManifoldPart2(input: String): Long {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    if (lines.isEmpty()) return 0L

    // Find start position 'S'
    var startCol = -1
    for (i in lines[0].indices) {
        if (lines[0][i] == 'S') {
            startCol = i
            break
        }
    }
    if (startCol == -1) return 0L

    // Map of col -> number of timelines at this position
    var currentTimelines = mutableMapOf(startCol to 1L)

    // Process each row from top to bottom
    for (row in 1 until lines.size) {
        val nextTimelines = mutableMapOf<Int, Long>()

        for ((col, count) in currentTimelines) {
            if (col >= 0 && col < lines[row].length) {
                if (lines[row][col] == '^') {
                    // Particle hits a splitter - timeline splits
                    println("Row $row, Col $col: $count timelines split")
                    // Add count timelines going left
                    if (col - 1 >= 0) {
                        nextTimelines[col - 1] = nextTimelines.getOrDefault(col - 1, 0L) + count
                    }
                    // Add count timelines going right
                    if (col + 1 < lines[row].length) {
                        nextTimelines[col + 1] = nextTimelines.getOrDefault(col + 1, 0L) + count
                    }
                } else {
                    // Particle continues downward in all timelines
                    nextTimelines[col] = nextTimelines.getOrDefault(col, 0L) + count
                }
            }
        }

        currentTimelines = nextTimelines
        println("After row $row: $currentTimelines")
        if (currentTimelines.isEmpty()) break
    }

    // Sum all timelines that completed the journey
    return currentTimelines.values.sum()
}

fun main() {
    val example = File("test_day7_example.txt").readText()
    val result = solveTachyonManifoldPart2(example)
    println("\nFinal result: $result (expected: 40)")
}
