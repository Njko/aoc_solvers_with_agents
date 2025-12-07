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
}
