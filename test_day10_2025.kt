import fr.nicolaslinard.koog.kmp.agents.solvearith.SolveArithTools

fun main() {
    val tools = SolveArithTools()

    // Test with examples from puzzle description
    val example1 = "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}"
    val example2 = "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}"
    val example3 = "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"

    val result1 = tools.solveFactoryLights(example1)
    val result2 = tools.solveFactoryLights(example2)
    val result3 = tools.solveFactoryLights(example3)

    println("Example 1: $result1 (expected: 2)")
    println("Example 2: $result2 (expected: 3)")
    println("Example 3: $result3 (expected: 2)")
    println("Total: ${result1 + result2 + result3} (expected: 7)")

    val allExamples = """
$example1
$example2
$example3
""".trim()

    val totalResult = tools.solveFactoryLights(allExamples)
    println("\nAll examples together: $totalResult (expected: 7)")
}
