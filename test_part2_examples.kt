import fr.nicolaslinard.koog.kmp.agents.solvearith.SolveArithTools

fun main() {
    val tools = SolveArithTools()

    val example1 = "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}"
    val example2 = "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}"
    val example3 = "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"

    println("Testing Part 2 examples:")
    println("Example 1: ${tools.solveFactoryJoltage(example1)} (expected: 10)")
    println("Example 2: ${tools.solveFactoryJoltage(example2)} (expected: 12)")
    println("Example 3: ${tools.solveFactoryJoltage(example3)} (expected: 11)")

    val allExamples = "$example1\n$example2\n$example3"
    println("\nAll examples: ${tools.solveFactoryJoltage(allExamples)} (expected: 33)")
}
