val testInput = """7,1
11,1
11,7
9,7
9,5
2,5
2,3
7,3"""

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
                println("Rectangle: ($x1,$y1) to ($x2,$y2) -> width=$width, height=$height, area=$area")
                maxArea = kotlin.math.max(maxArea, area)
            }
        }
    }

    return maxArea
}

val result = solveMovieTheater(testInput)
println("\nResult: $result")
println("Expected: 50")
