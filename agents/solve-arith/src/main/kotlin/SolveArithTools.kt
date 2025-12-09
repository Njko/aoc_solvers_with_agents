package fr.nicolaslinard.koog.kmp.agents.solvearith

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Arithmetic/regex solvers for simple AoC puzzles. Includes 2023 Day 1 Part 1 calibration sum and 2025 Day 1 Part 1 dial simulation.")
class SolveArithTools : ToolSet {

    @Tool
    @LLMDescription(
        "Solves the calibration sum (AoC 2023 Day 1 Part 1 style): for each line, form a two-digit number using the first and last digit present (duplicate the only digit if there is just one), then sum across lines. Returns the sum as Int."
    )
    fun solveCalibrationSum(input: String): Int {
        val lines = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .trimEnd('\n', '\r')
            .split('\n')
        var sum = 0
        for (line in lines) {
            val ds = line.filter { it.isDigit() }
            if (ds.isEmpty()) continue
            val first = ds.first().digitToInt()
            val last = ds.last().digitToInt()
            sum += first * 10 + last
        }
        return sum
    }

    @Tool
    @LLMDescription(
        "Solves the dial simulation (AoC 2025 Day 1 Part 1): simulate a combination dial (0-99) starting at 50. Each line is a rotation: L/R followed by distance. Count how many times the dial lands on 0. Returns the count as Int."
    )
    fun solveDialSimulation(input: String): Int {
        val lines = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .trimEnd('\n', '\r')
            .split('\n')
            .filter { it.isNotBlank() }

        var position = 50
        var count = 0

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val direction = trimmed[0]
            val distance = trimmed.substring(1).toIntOrNull() ?: continue

            when (direction) {
                'L', 'l' -> position = (position - distance).mod(100)
                'R', 'r' -> position = (position + distance).mod(100)
            }

            if (position == 0) {
                count++
            }
        }

        return count
    }

    @Tool
    @LLMDescription(
        "Solves the dial simulation Part 2 (AoC 2025 Day 1 Part 2): count how many times any click causes the dial to point at 0, including during rotations. Each rotation step is counted. Returns the count as Int."
    )
    fun solveDialSimulationPart2(input: String): Int {
        val lines = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .trimEnd('\n', '\r')
            .split('\n')
            .filter { it.isNotBlank() }

        var position = 50
        var count = 0

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val direction = trimmed[0]
            val distance = trimmed.substring(1).toIntOrNull() ?: continue

            // Count how many times we pass through 0 during this rotation
            when (direction) {
                'L', 'l' -> {
                    // Going left (counterclockwise): count times we cross 0
                    // We visit positions: (pos-1), (pos-2), ..., (pos-distance) mod 100
                    // Special case: if starting at 0, don't count the start
                    val crosses = if (position == 0) {
                        distance / 100
                    } else if (distance >= position) {
                        1 + (distance - position) / 100
                    } else {
                        0
                    }
                    count += crosses
                    position = (position - distance).mod(100)
                }
                'R', 'r' -> {
                    // Going right (clockwise): count times we cross 0
                    // We cross 0 when we wrap from 99 to 0
                    count += (position + distance) / 100
                    position = (position + distance) % 100
                }
            }
        }

        return count
    }

    @Tool
    @LLMDescription(
        "Solves vertical math worksheet (AoC 2025 Day 6): numbers arranged in vertical columns with operations at bottom. Parse each column, apply operation, sum all results. Returns the grand total as Long."
    )
    fun solveVerticalMathWorksheet(input: String): Long {
        val lines = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .split('\n')
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) return 0

        // Find the width of the input (max line length)
        val width = lines.maxOf { it.length }

        // Transpose to get columns
        val columns = mutableListOf<MutableList<Char>>()
        for (col in 0 until width) {
            val column = mutableListOf<Char>()
            for (line in lines) {
                if (col < line.length) {
                    column.add(line[col])
                }
            }
            columns.add(column)
        }

        // Group consecutive non-space columns into problems
        val problems = mutableListOf<List<List<Char>>>()
        var currentProblem = mutableListOf<List<Char>>()

        for (column in columns) {
            val isAllSpaces = column.all { it == ' ' }
            if (isAllSpaces) {
                if (currentProblem.isNotEmpty()) {
                    problems.add(currentProblem.toList())
                    currentProblem = mutableListOf()
                }
            } else {
                currentProblem.add(column)
            }
        }
        if (currentProblem.isNotEmpty()) {
            problems.add(currentProblem.toList())
        }

        // Solve each problem
        var grandTotal = 0L

        for (problem in problems) {
            if (problem.isEmpty()) continue

            // Read row-by-row from the problem columns
            val numRows = problem[0].size
            val numbers = mutableListOf<Long>()
            var operator: Char? = null

            for (row in 0 until numRows) {
                // Extract the text from this row across all columns in the problem
                val rowText = StringBuilder()
                for (col in problem) {
                    if (row < col.size) {
                        rowText.append(col[row])
                    }
                }

                val line = rowText.toString().trim()

                // Check if this row contains the operator
                if (line.contains('*') || line.contains('+')) {
                    operator = line.find { it == '*' || it == '+' }
                } else if (line.isNotEmpty()) {
                    // Try to parse as a number
                    line.toLongOrNull()?.let { numbers.add(it) }
                }
            }

            // Apply operation
            if (numbers.isNotEmpty() && operator != null) {
                val result = when (operator) {
                    '+' -> numbers.sum()
                    '*' -> numbers.reduce { acc, n -> acc * n }
                    else -> 0L
                }
                grandTotal += result
            }
        }

        return grandTotal
    }

    @Tool
    @LLMDescription(
        "Solves vertical math worksheet Part 2 (AoC 2025 Day 6 Part 2): cephalopod math is written right-to-left in columns. Each column forms one number (top=most significant, bottom=least significant). Process columns right-to-left. Returns the grand total as Long."
    )
    fun solveVerticalMathWorksheetPart2(input: String): Long {
        val lines = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .split('\n')
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) return 0

        // Find the width of the input (max line length)
        val width = lines.maxOf { it.length }

        // Transpose to get columns
        val columns = mutableListOf<MutableList<Char>>()
        for (col in 0 until width) {
            val column = mutableListOf<Char>()
            for (line in lines) {
                if (col < line.length) {
                    column.add(line[col])
                }
            }
            columns.add(column)
        }

        // Group consecutive non-space columns into problems
        val problems = mutableListOf<List<List<Char>>>()
        var currentProblem = mutableListOf<List<Char>>()

        for (column in columns) {
            val isAllSpaces = column.all { it == ' ' }
            if (isAllSpaces) {
                if (currentProblem.isNotEmpty()) {
                    problems.add(currentProblem.toList())
                    currentProblem = mutableListOf()
                }
            } else {
                currentProblem.add(column)
            }
        }
        if (currentProblem.isNotEmpty()) {
            problems.add(currentProblem.toList())
        }

        // Solve each problem
        var grandTotal = 0L

        for (problem in problems) {
            if (problem.isEmpty()) continue

            val numbers = mutableListOf<Long>()
            var operator: Char? = null

            // Find operator in leftmost column (where it should be for Part 2)
            val leftmostColumn = problem.first()
            val opChar = leftmostColumn.lastOrNull()
            if (opChar == '*' || opChar == '+') {
                operator = opChar
            }

            // Process ALL columns (including the one with operator) to extract numbers
            for (column in problem) {
                // Read column top-to-bottom to form number (top = most significant)
                val digitChars = mutableListOf<Char>()
                for (rowIdx in column.indices) {
                    val ch = column[rowIdx]
                    if (ch.isDigit()) {
                        digitChars.add(ch)
                    }
                }

                if (digitChars.isNotEmpty()) {
                    val number = digitChars.joinToString("").toLongOrNull() ?: 0L
                    numbers.add(number)
                }
            }

            // Apply operation
            if (numbers.isNotEmpty() && operator != null) {
                val result = when (operator) {
                    '+' -> numbers.sum()
                    '*' -> numbers.reduce { acc, n -> acc * n }
                    else -> 0L
                }
                grandTotal += result
            }
        }

        return grandTotal
    }

    @Tool
    @LLMDescription(
        "Solves tachyon manifold simulation (AoC 2025 Day 7): A beam starts at 'S' and moves downward. When it hits a splitter '^', it stops and emits two new beams from the immediate left and right. Count how many times the beam is split. Returns the split count as Int."
    )
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
            if (activeBeams.isEmpty()) break
        }

        return splitCount
    }

    @Tool
    @LLMDescription(
        "Solves quantum tachyon manifold (AoC 2025 Day 7 Part 2): A single particle takes BOTH paths at each splitter, creating multiple timelines. Count the total number of distinct timelines after the particle completes all journeys. Returns timeline count as Long."
    )
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

        // Map of (row, col) -> number of timelines at this position
        var currentTimelines = mutableMapOf(startCol to 1L)

        // Process each row from top to bottom
        for (row in 1 until lines.size) {
            val nextTimelines = mutableMapOf<Int, Long>()

            for ((col, count) in currentTimelines) {
                if (col >= 0 && col < lines[row].length) {
                    if (lines[row][col] == '^') {
                        // Particle hits a splitter - timeline splits
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
            if (currentTimelines.isEmpty()) break
        }

        // Sum all timelines that completed the journey
        return currentTimelines.values.sum()
    }

    @Tool
    @LLMDescription(
        "Solves gift shop invalid IDs (AoC 2025 Day 2 Part 1): Find all IDs within given ranges that are made of a digit sequence repeated exactly twice (e.g., 11, 6464, 123123). Sum all invalid IDs. Returns the sum as Long."
    )
    fun solveGiftShopPart1(input: String): Long {
        val ranges = input.trim().replace("\n", "").replace("\r", "").split(',')
        var sum = 0L

        for (range in ranges) {
            val parts = range.trim().split('-')
            if (parts.size != 2) continue
            val start = parts[0].toLongOrNull() ?: continue
            val end = parts[1].toLongOrNull() ?: continue

            for (id in start..end) {
                val idStr = id.toString()
                // Check if it's a repeated sequence (exactly twice)
                val len = idStr.length
                if (len % 2 == 0) {
                    val halfLen = len / 2
                    val firstHalf = idStr.substring(0, halfLen)
                    val secondHalf = idStr.substring(halfLen)
                    if (firstHalf == secondHalf && firstHalf[0] != '0') {
                        sum += id
                    }
                }
            }
        }

        return sum
    }

    @Tool
    @LLMDescription(
        "Solves gift shop invalid IDs Part 2 (AoC 2025 Day 2 Part 2): Find all IDs that are made of a digit sequence repeated at least twice (e.g., 111, 12121212, 123123123). Sum all invalid IDs. Returns the sum as Long."
    )
    fun solveGiftShopPart2(input: String): Long {
        val ranges = input.trim().replace("\n", "").replace("\r", "").split(',')
        var sum = 0L

        for (range in ranges) {
            val parts = range.trim().split('-')
            if (parts.size != 2) continue
            val start = parts[0].toLongOrNull() ?: continue
            val end = parts[1].toLongOrNull() ?: continue

            for (id in start..end) {
                val idStr = id.toString()
                // Check if it's a repeated sequence (at least twice)
                var isRepeated = false
                for (patternLen in 1..idStr.length / 2) {
                    if (idStr.length % patternLen == 0) {
                        val pattern = idStr.substring(0, patternLen)
                        if (pattern[0] != '0') {
                            var matches = true
                            for (i in patternLen until idStr.length step patternLen) {
                                if (idStr.substring(i, i + patternLen) != pattern) {
                                    matches = false
                                    break
                                }
                            }
                            if (matches) {
                                isRepeated = true
                                break
                            }
                        }
                    }
                }
                if (isRepeated) {
                    sum += id
                }
            }
        }

        return sum
    }

    @Tool
    @LLMDescription(
        "Solves battery joltage Part 1 (AoC 2025 Day 3 Part 1): For each bank (line of digits), select exactly 2 batteries to maximize joltage. Sum all maximum joltages. Returns the sum as Long."
    )
    fun solveBatteryJoltagePart1(input: String): Long {
        val lines = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .split('\n')
            .filter { it.isNotEmpty() }

        var totalJoltage = 0L

        for (line in lines) {
            var maxJoltage = 0L
            // Try all pairs of positions
            for (i in 0 until line.length - 1) {
                for (j in i + 1 until line.length) {
                    val joltage = "${line[i]}${line[j]}".toLongOrNull() ?: 0L
                    if (joltage > maxJoltage) {
                        maxJoltage = joltage
                    }
                }
            }
            totalJoltage += maxJoltage
        }

        return totalJoltage
    }

    @Tool
    @LLMDescription(
        "Solves battery joltage Part 2 (AoC 2025 Day 3 Part 2): For each bank, select exactly 12 batteries to maximize joltage (12-digit number). Sum all maximum joltages. Returns the sum as Long."
    )
    fun solveBatteryJoltagePart2(input: String): Long {
        val lines = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .split('\n')
            .filter { it.isNotEmpty() }

        var totalJoltage = 0L

        for (line in lines) {
            // Greedy approach: for each position in result, pick the largest digit
            // from indices after the last selected, leaving enough digits for remaining positions
            val digits = line.toList()
            val result = StringBuilder()
            var startIdx = 0  // start searching from here

            for (resultPos in 0 until 12) {
                val needed = 12 - resultPos - 1  // how many more we need after this
                var bestIdx = -1
                var bestDigit = '0'

                // Only search from startIdx onward (maintain order)
                for (i in startIdx until digits.size) {
                    // Check if there are enough digits remaining after this position
                    val remainingAfter = digits.size - i - 1

                    if (remainingAfter >= needed) {
                        if (digits[i] > bestDigit) {
                            bestDigit = digits[i]
                            bestIdx = i
                        }
                    }
                }

                if (bestIdx >= 0) {
                    result.append(digits[bestIdx])
                    startIdx = bestIdx + 1  // next search starts after this
                }
            }

            val joltage = result.toString().toLongOrNull() ?: 0L
            totalJoltage += joltage
        }

        return totalJoltage
    }

    @Tool
    @LLMDescription(
        "Solves forklift accessible paper rolls Part 1 (AoC 2025 Day 4 Part 1): Count rolls (@) with fewer than 4 adjacent rolls in 8 directions. Returns count as Int."
    )
    fun solveForkliftAccessPart1(input: String): Int {
        val grid = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .split('\n')
            .filter { it.isNotEmpty() }

        var accessibleCount = 0

        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col] == '@') {
                    var adjacentCount = 0
                    // Check 8 adjacent positions
                    for (dr in -1..1) {
                        for (dc in -1..1) {
                            if (dr == 0 && dc == 0) continue
                            val nr = row + dr
                            val nc = col + dc
                            if (nr in grid.indices && nc in grid[nr].indices && grid[nr][nc] == '@') {
                                adjacentCount++
                            }
                        }
                    }
                    if (adjacentCount < 4) {
                        accessibleCount++
                    }
                }
            }
        }

        return accessibleCount
    }

    @Tool
    @LLMDescription(
        "Solves forklift accessible paper rolls Part 2 (AoC 2025 Day 4 Part 2): Iteratively remove accessible rolls (<4 adjacent) until none remain. Count total removed. Returns count as Int."
    )
    fun solveForkliftAccessPart2(input: String): Int {
        val grid = input
            .replace("\r\n", "\n").replace("\r", "\n")
            .split('\n')
            .filter { it.isNotEmpty() }
            .map { it.toCharArray() }

        var totalRemoved = 0

        while (true) {
            val toRemove = mutableListOf<Pair<Int, Int>>()

            // Find all accessible rolls
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    if (grid[row][col] == '@') {
                        var adjacentCount = 0
                        for (dr in -1..1) {
                            for (dc in -1..1) {
                                if (dr == 0 && dc == 0) continue
                                val nr = row + dr
                                val nc = col + dc
                                if (nr in grid.indices && nc in grid[nr].indices && grid[nr][nc] == '@') {
                                    adjacentCount++
                                }
                            }
                        }
                        if (adjacentCount < 4) {
                            toRemove.add(Pair(row, col))
                        }
                    }
                }
            }

            if (toRemove.isEmpty()) break

            // Remove all accessible rolls
            for ((row, col) in toRemove) {
                grid[row][col] = '.'
            }

            totalRemoved += toRemove.size
        }

        return totalRemoved
    }

    @Tool
    @LLMDescription(
        "Solves playground junction boxes (AoC 2025 Day 8 Part 1): Connect 1000 closest pairs of junction boxes, find three largest circuits, multiply them. Returns product as Long."
    )
    fun solvePlaygroundJunctionBoxes(input: String): Long {
        val lines = input
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .trimEnd('\n', '\r')
            .split('\n')
            .filter { it.isNotBlank() }

        // Parse coordinates
        val boxes = mutableListOf<Triple<Int, Int, Int>>()
        for (line in lines) {
            val parts = line.split(',')
            if (parts.size == 3) {
                val x = parts[0].trim().toIntOrNull() ?: continue
                val y = parts[1].trim().toIntOrNull() ?: continue
                val z = parts[2].trim().toIntOrNull() ?: continue
                boxes.add(Triple(x, y, z))
            }
        }

        if (boxes.size < 2) return 0L

        // Calculate all pairwise distances
        data class Edge(val i: Int, val j: Int, val distSq: Long)
        val edges = mutableListOf<Edge>()

        for (i in boxes.indices) {
            for (j in i + 1 until boxes.size) {
                val (x1, y1, z1) = boxes[i]
                val (x2, y2, z2) = boxes[j]
                val dx = (x2 - x1).toLong()
                val dy = (y2 - y1).toLong()
                val dz = (z2 - z1).toLong()
                val distSq = dx * dx + dy * dy + dz * dz
                edges.add(Edge(i, j, distSq))
            }
        }

        // Sort by distance
        edges.sortBy { it.distSq }

        // Union-Find
        val parent = IntArray(boxes.size) { it }
        val size = IntArray(boxes.size) { 1 }

        fun find(x: Int): Int {
            if (parent[x] != x) {
                parent[x] = find(parent[x])
            }
            return parent[x]
        }

        fun union(x: Int, y: Int) {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX != rootY) {
                if (size[rootX] < size[rootY]) {
                    parent[rootX] = rootY
                    size[rootY] += size[rootX]
                } else {
                    parent[rootY] = rootX
                    size[rootX] += size[rootY]
                }
            }
        }

        // Connect 1000 closest pairs
        val connectionsToMake = minOf(1000, edges.size)
        for (i in 0 until connectionsToMake) {
            val edge = edges[i]
            union(edge.i, edge.j)
        }

        // Find all circuit sizes
        val circuitSizes = mutableMapOf<Int, Int>()
        for (i in boxes.indices) {
            val root = find(i)
            circuitSizes[root] = size[root]
        }

        // Get three largest
        val sorted = circuitSizes.values.sortedDescending()
        if (sorted.size < 3) return 0L

        return sorted[0].toLong() * sorted[1].toLong() * sorted[2].toLong()
    }

    @Tool
    @LLMDescription(
        "Solves playground junction boxes Part 2 (AoC 2025 Day 8 Part 2): Connect pairs until all boxes form one circuit, multiply X coordinates of last two boxes connected. Returns product as Long."
    )
    fun solvePlaygroundJunctionBoxesPart2(input: String): Long {
        val lines = input
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .trimEnd('\n', '\r')
            .split('\n')
            .filter { it.isNotBlank() }

        // Parse coordinates
        val boxes = mutableListOf<Triple<Int, Int, Int>>()
        for (line in lines) {
            val parts = line.split(',')
            if (parts.size == 3) {
                val x = parts[0].trim().toIntOrNull() ?: continue
                val y = parts[1].trim().toIntOrNull() ?: continue
                val z = parts[2].trim().toIntOrNull() ?: continue
                boxes.add(Triple(x, y, z))
            }
        }

        if (boxes.size < 2) return 0L

        // Calculate all pairwise distances
        data class Edge(val i: Int, val j: Int, val distSq: Long)
        val edges = mutableListOf<Edge>()

        for (i in boxes.indices) {
            for (j in i + 1 until boxes.size) {
                val (x1, y1, z1) = boxes[i]
                val (x2, y2, z2) = boxes[j]
                val dx = (x2 - x1).toLong()
                val dy = (y2 - y1).toLong()
                val dz = (z2 - z1).toLong()
                val distSq = dx * dx + dy * dy + dz * dz
                edges.add(Edge(i, j, distSq))
            }
        }

        // Sort by distance
        edges.sortBy { it.distSq }

        // Union-Find
        val parent = IntArray(boxes.size) { it }
        val size = IntArray(boxes.size) { 1 }

        fun find(x: Int): Int {
            if (parent[x] != x) {
                parent[x] = find(parent[x])
            }
            return parent[x]
        }

        fun union(x: Int, y: Int): Boolean {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX != rootY) {
                if (size[rootX] < size[rootY]) {
                    parent[rootX] = rootY
                    size[rootY] += size[rootX]
                } else {
                    parent[rootY] = rootX
                    size[rootX] += size[rootY]
                }
                return true
            }
            return false
        }

        // Connect pairs until all boxes are in one circuit
        var lastI = -1
        var lastJ = -1

        for (edge in edges) {
            if (union(edge.i, edge.j)) {
                lastI = edge.i
                lastJ = edge.j

                // Check if all boxes are in one circuit
                val root = find(0)
                if (size[root] == boxes.size) {
                    // All connected!
                    break
                }
            }
        }

        if (lastI == -1 || lastJ == -1) return 0L

        // Return product of X coordinates
        val x1 = boxes[lastI].first
        val x2 = boxes[lastJ].first
        return x1.toLong() * x2.toLong()
    }

    @Tool
    @LLMDescription("Solves movie theater tile floor problem (AoC 2025 Day 9 Part 1): Given red tile coordinates, find the largest rectangle that uses red tiles for two opposite corners. Returns maximum area as Long.")
    fun solveMovieTheater(input: String): Long {
        val lines = input.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r').split('\n').filter { it.isNotBlank() }

        // Parse coordinates
        val tiles = mutableListOf<Pair<Int, Int>>()
        for (line in lines) {
            val parts = line.split(',')
            if (parts.size == 2) {
                val x = parts[0].trim().toIntOrNull() ?: continue
                val y = parts[1].trim().toIntOrNull() ?: continue
                tiles.add(Pair(x, y))
            }
        }

        if (tiles.size < 2) return 0L

        // Find largest rectangle using any two tiles as opposite corners
        var maxArea = 0L
        for (i in tiles.indices) {
            for (j in i + 1 until tiles.size) {
                val (x1, y1) = tiles[i]
                val (x2, y2) = tiles[j]

                // Only consider points that form opposite corners (different x and y)
                if (x1 != x2 && y1 != y2) {
                    // Area includes the boundary tiles
                    val width = kotlin.math.abs(x2 - x1) + 1
                    val height = kotlin.math.abs(y2 - y1) + 1
                    val area = width.toLong() * height.toLong()
                    maxArea = kotlin.math.max(maxArea, area)
                }
            }
        }

        return maxArea
    }

    @Tool
    @LLMDescription("Solves movie theater part 2 (AoC 2025 Day 9 Part 2): Red tiles form a loop connected by green tiles. Tiles inside the loop are also green. Find largest rectangle using red corners that only contains red/green tiles.")
    fun solveMovieTheaterPart2(input: String): Long {
        val lines = input.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r').split('\n').filter { it.isNotBlank() }

        // Parse red tile coordinates in order
        val redTiles = mutableListOf<Pair<Int, Int>>()
        for (line in lines) {
            val parts = line.split(',')
            if (parts.size == 2) {
                val x = parts[0].trim().toIntOrNull() ?: continue
                val y = parts[1].trim().toIntOrNull() ?: continue
                redTiles.add(Pair(x, y))
            }
        }

        if (redTiles.size < 2) return 0L

        // Coordinate compression
        val allX = redTiles.map { it.first }.toSet().sorted()
        val allY = redTiles.map { it.second }.toSet().sorted()
        val xMap = allX.withIndex().associate { it.value to it.index }
        val yMap = allY.withIndex().associate { it.value to it.index }

        val compressedRed = redTiles.map { Pair(xMap[it.first]!!, yMap[it.second]!!) }
        val compressedRedSet = compressedRed.toSet()

        // Build green tiles on edges
        val greenSet = mutableSetOf<Pair<Int, Int>>()
        for (i in compressedRed.indices) {
            val (cx1, cy1) = compressedRed[i]
            val (cx2, cy2) = compressedRed[(i + 1) % compressedRed.size]

            if (cx1 == cx2) {
                val minCY = kotlin.math.min(cy1, cy2)
                val maxCY = kotlin.math.max(cy1, cy2)
                for (cy in minCY..maxCY) {
                    val tile = Pair(cx1, cy)
                    if (tile !in compressedRedSet) greenSet.add(tile)
                }
            } else if (cy1 == cy2) {
                val minCX = kotlin.math.min(cx1, cx2)
                val maxCX = kotlin.math.max(cx1, cx2)
                for (cx in minCX..maxCX) {
                    val tile = Pair(cx, cy1)
                    if (tile !in compressedRedSet) greenSet.add(tile)
                }
            }
        }

        // Mark inside points as green
        val minCX = 0
        val maxCX = allX.size - 1
        val minCY = 0
        val maxCY = allY.size - 1

        // Use scanline to mark inside efficiently
        for (cy in minCY..maxCY) {
            val crossings = mutableListOf<Int>()
            for (i in redTiles.indices) {
                val p1 = redTiles[i]
                val p2 = redTiles[(i + 1) % redTiles.size]
                val y = allY[cy]

                if ((p1.second <= y && p2.second > y) || (p2.second <= y && p1.second > y)) {
                    val xIntersect = p1.first + (y - p1.second) * (p2.first - p1.first) / (p2.second - p1.second)
                    val cxIntersect = xMap[allX.binarySearch(xIntersect).let { if (it >= 0) allX[it] else allX[-(it + 1)] }]!!
                    crossings.add(cxIntersect)
                }
            }
            crossings.sort()

            var inside = false
            var lastX = minCX
            for (cx in crossings) {
                if (inside) {
                    for (x in lastX until cx) {
                        val p = Pair(x, cy)
                        if (p !in compressedRedSet) greenSet.add(p)
                    }
                }
                inside = !inside
                lastX = cx
            }
        }

        val validSet = compressedRedSet + greenSet

        // Find largest rectangle in compressed space
        var maxArea = 0L
        for (i in compressedRed.indices) {
            for (j in i + 1 until compressedRed.size) {
                val (cx1, cy1) = compressedRed[i]
                val (cx2, cy2) = compressedRed[j]

                if (cx1 != cx2 && cy1 != cy2) {
                    val minCX = kotlin.math.min(cx1, cx2)
                    val maxCX = kotlin.math.max(cx1, cx2)
                    val minCY = kotlin.math.min(cy1, cy2)
                    val maxCY = kotlin.math.max(cy1, cy2)

                    // Check compressed rectangle
                    var allValid = true
                    outer@ for (cx in minCX..maxCX) {
                        for (cy in minCY..maxCY) {
                            if (Pair(cx, cy) !in validSet) {
                                allValid = false
                                break@outer
                            }
                        }
                    }

                    if (allValid) {
                        // Calculate area in original coordinates
                        val width = allX[maxCX] - allX[minCX] + 1
                        val height = allY[maxCY] - allY[minCY] + 1
                        val area = width.toLong() * height.toLong()
                        maxArea = kotlin.math.max(maxArea, area)
                    }
                }
            }
        }

        return maxArea
    }

    private fun isInsidePolygon(point: Pair<Int, Int>, polygon: List<Pair<Int, Int>>): Boolean {
        // Ray casting algorithm
        val (px, py) = point
        var inside = false
        var j = polygon.size - 1

        for (i in polygon.indices) {
            val (xi, yi) = polygon[i]
            val (xj, yj) = polygon[j]

            if ((yi > py) != (yj > py) &&
                px < (xj - xi) * (py - yi) / (yj - yi) + xi) {
                inside = !inside
            }
            j = i
        }

        return inside
    }
}
