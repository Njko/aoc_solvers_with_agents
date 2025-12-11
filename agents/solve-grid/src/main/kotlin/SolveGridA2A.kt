package fr.nicolaslinard.koog.kmp.agents.solvegrid

import A2AHandler
import A2ARouter
import A2ATaskRequest
import A2ATaskResult
import A2AStatus
import GridTools

/**
 * Grid solver A2A handlers. Default demo action: AoC 2024 Day 4 Part 1 — count occurrences of the word "XMAS".
 */
object SolveGridA2A {
    fun register() {
        val cap = "aoc.solve.grid"
        A2ARouter.register(cap, "xmasCount", xmasCount())
        A2ARouter.register(cap, "trailheadScore", trailheadScore())
    }

    private fun xmasCount(): A2AHandler = { req: A2ATaskRequest ->
        val grid = req.payload ?: ""
        try {
            val tools = GridTools()
            val value = tools.countWordOccurrences(grid, "XMAS")
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"grid_xmas\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun trailheadScore(): A2AHandler = { req: A2ATaskRequest ->
        val grid = req.payload ?: ""
        try {
            val value = calculateTrailheadScores(grid)
            val json = "{" +
                    "\"part\":1," +
                    "\"value\":$value," +
                    "\"method\":\"trailhead_score\"" +
                    "}"
            A2ATaskResult(req.correlationId, A2AStatus.OK, payload = json)
        } catch (t: Throwable) {
            A2ATaskResult(req.correlationId, A2AStatus.ERROR, error = t.message ?: t.toString())
        }
    }

    private fun calculateTrailheadScores(gridText: String): Int {
        val lines = gridText.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r').split('\n').filter { it.isNotEmpty() }
        if (lines.isEmpty()) return 0

        val grid = lines.map { it.toCharArray() }
        val height = grid.size
        val width = grid.maxOfOrNull { it.size } ?: 0
        if (width == 0) return 0

        // Find all trailheads (positions with height 0)
        val trailheads = mutableListOf<Pair<Int, Int>>()
        for (row in 0 until height) {
            for (col in 0 until grid[row].size) {
                if (grid[row][col] == '0') {
                    trailheads.add(Pair(row, col))
                }
            }
        }

        // For each trailhead, count reachable height-9 positions
        var totalScore = 0
        for (trailhead in trailheads) {
            totalScore += countReachableNines(grid, trailhead)
        }

        return totalScore
    }

    private fun countReachableNines(grid: List<CharArray>, start: Pair<Int, Int>): Int {
        val height = grid.size
        val reachableNines = mutableSetOf<Pair<Int, Int>>()

        // BFS to find all reachable positions with height 9
        val queue = ArrayDeque<Pair<Int, Int>>()
        val visited = mutableSetOf<Pair<Int, Int>>()

        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val (row, col) = queue.removeFirst()
            val currentHeight = grid[row][col].digitToInt()

            // If we reached height 9, add to reachable nines
            if (currentHeight == 9) {
                reachableNines.add(Pair(row, col))
                continue
            }

            // Explore 4 adjacent cells (up, down, left, right)
            val directions = listOf(
                Pair(-1, 0),  // up
                Pair(1, 0),   // down
                Pair(0, -1),  // left
                Pair(0, 1)    // right
            )

            for ((dr, dc) in directions) {
                val newRow = row + dr
                val newCol = col + dc

                // Check bounds (handle variable width)
                if (newRow in 0 until height && newCol >= 0 && newCol < grid[newRow].size) {
                    val newPos = Pair(newRow, newCol)

                    // Check if not visited and height increases by exactly 1
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
}
