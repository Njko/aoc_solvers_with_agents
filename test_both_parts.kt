import fr.nicolaslinard.koog.kmp.agents.solvearith.SolveArithTools

fun main() {
    val tools = SolveArithTools()

    val testInput = "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}"

    println("Testing with: $testInput")
    println()

    val part1Result = tools.solveFactoryLights(testInput)
    println("Part 1 (lights): $part1Result")

    val part2Result = tools.solveFactoryJoltage(testInput)
    println("Part 2 (joltage): $part2Result")

    println()
    println("Part 2 expected: 10")
    println("Are they the same? ${part1Result == part2Result}")
}
