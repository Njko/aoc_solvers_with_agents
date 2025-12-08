val testInput = """162,817,812
57,618,57
906,360,560
805,96,715
425,690,689
431,825,988
262,595,750
777,278,650
611,775,732
222,537,804
531,813,705
99,521,592
343,603,700
930,291,666
726,322,714
582,773,662
810,122,697
475,631,721
349,582,768
464,646,659"""

fun solvePlaygroundJunctionBoxes(input: String): Long {
    val lines = input.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r').split('\n').filter { it.isNotBlank() }

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

    println("Parsed ${boxes.size} boxes")

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

    println("Total edges: ${edges.size}")
    edges.sortBy { it.distSq }

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

    // Connect 10 closest pairs for the example (not 1000)
    val connectionsToMake = minOf(10, edges.size)
    println("Connecting $connectionsToMake closest pairs")
    for (i in 0 until connectionsToMake) {
        val edge = edges[i]
        union(edge.i, edge.j)
    }

    val circuitSizes = mutableMapOf<Int, Int>()
    for (i in boxes.indices) {
        val root = find(i)
        circuitSizes[root] = size[root]
    }

    val sorted = circuitSizes.values.sortedDescending()
    println("Circuit sizes: $sorted")
    println("Top 3: ${sorted.take(3)}")

    if (sorted.size < 3) return 0L

    return sorted[0].toLong() * sorted[1].toLong() * sorted[2].toLong()
}

val result = solvePlaygroundJunctionBoxes(testInput)
println("\nResult: $result")
println("Expected: 40 (5 × 4 × 2)")
