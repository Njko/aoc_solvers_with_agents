package fr.nicolaslinard.koog.kmp.agents.solvearith

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Arithmetic/regex solvers for simple AoC puzzles. Includes 2023 Day 1 Part 1 calibration sum.")
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
}
