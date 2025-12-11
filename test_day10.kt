import fr.nicolaslinard.koog.kmp.agents.solvegrid.SolveGridA2A

fun main() {
    // Example from the puzzle description
    val example = """
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
""".trim()

    // The expected result for this example is 36
    println("Testing Day 10 Part 1 with example...")

    // Since we can't directly call the private method, let's test via A2A
    val grid = example
    val lines = grid.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r').split('\n')

    // Manual test: find trailheads and count reachable nines
    val gridArray = lines.map { it.toCharArray() }
    val height = gridArray.size
    val width = gridArray[0].size

    println("Grid size: $height x $width")

    // Find trailheads
    val trailheads = mutableListOf<Pair<Int, Int>>()
    for (row in 0 until height) {
        for (col in 0 until width) {
            if (gridArray[row][col] == '0') {
                trailheads.add(Pair(row, col))
                println("Found trailhead at ($row, $col)")
            }
        }
    }

    println("Found ${trailheads.size} trailheads")

    // For each trailhead, count reachable nines
    var totalScore = 0
    for (trailhead in trailheads) {
        val score = countReachableNines(gridArray, trailhead)
        println("Trailhead at $trailhead has score $score")
        totalScore += score
    }

    println("\nTotal score: $totalScore")
    println("Expected: 36")
    println("Match: ${totalScore == 36}")
}

fun countReachableNines(grid: List<CharArray>, start: Pair<Int, Int>): Int {
    val height = grid.size
    val width = grid[0].size
    val reachableNines = mutableSetOf<Pair<Int, Int>>()

    val queue = ArrayDeque<Pair<Int, Int>>()
    val visited = mutableSetOf<Pair<Int, Int>>()

    queue.add(start)
    visited.add(start)

    while (queue.isNotEmpty()) {
        val (row, col) = queue.removeFirst()
        val currentHeight = grid[row][col].digitToInt()

        if (currentHeight == 9) {
            reachableNines.add(Pair(row, col))
            continue
        }

        val directions = listOf(
            Pair(-1, 0),  // up
            Pair(1, 0),   // down
            Pair(0, -1),  // left
            Pair(0, 1)    // right
        )

        for ((dr, dc) in directions) {
            val newRow = row + dr
            val newCol = col + dc

            if (newRow in 0 until height && newCol in 0 until width) {
                val newPos = Pair(newRow, newCol)

                if (newPos !in visited && grid[newRow][newCol].isDigit()) {
                    val newHeight = grid[newRow][newCol].digitToInt()
                    if (newHeight == currentHeight + 1) {
                        visited.add(newPos)
                        queue.add(newPos)
                    }
                }
            }
        }
    }

    return reachableNines.size
}
