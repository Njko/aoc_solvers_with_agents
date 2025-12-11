import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Graph utilities for AoC puzzles (parsing and path counting).")
class GraphTools : ToolSet {

    /**
     * Compte le nombre de chemins de 'start' à 'end' (AoC 2021 Day 12 Part 1).
     * Règle: les petites grottes (lowercase) ne peuvent être visitées qu'une seule fois.
     * Entrée: texte multi‑lignes avec des arêtes non orientées au format "a-b".
     */
    @Tool
    @LLMDescription("Counts paths from 'start' to 'end' where lowercase nodes are visited at most once (AoC 2021 Day 12 Part 1). Input is lines like 'a-b'.")
    fun countCavePathsPart1(input: String): Int {
        val edges = parseEdges(input)
        val adj = buildAdjacency(edges)
        return dfsCountPathsPart1(adj, "start", "end")
    }

    private fun parseEdges(text: String): List<Pair<String, String>> {
        if (text.isBlank()) return emptyList()
        val norm = text.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r')
        val result = ArrayList<Pair<String, String>>()
        for (line in norm.split('\n')) {
            if (line.isBlank()) continue
            val parts = line.trim().split('-')
            if (parts.size != 2) continue
            val a = parts[0].trim()
            val b = parts[1].trim()
            if (a.isEmpty() || b.isEmpty()) continue
            result.add(a to b)
        }
        return result
    }

    private fun buildAdjacency(edges: List<Pair<String, String>>): Map<String, List<String>> {
        val map = LinkedHashMap<String, MutableList<String>>()
        for ((a, b) in edges) {
            map.getOrPut(a) { mutableListOf() }.add(b)
            map.getOrPut(b) { mutableListOf() }.add(a)
        }
        // Optionally sort neighbors for determinism
        return map.mapValues { it.value.sorted() }
    }

    private fun isSmall(name: String): Boolean = name.lowercase() == name

    private fun dfsCountPathsPart1(adj: Map<String, List<String>>, start: String, end: String): Int {
        var count = 0
        fun dfs(node: String, visitedSmall: MutableSet<String>) {
            if (node == end) {
                count++
                return
            }
            val neighbors = adj[node] ?: return
            for (n in neighbors) {
                if (n == start) continue // never go back to start
                val small = isSmall(n)
                if (small && n in visitedSmall) continue
                if (small) {
                    visitedSmall.add(n)
                    dfs(n, visitedSmall)
                    visitedSmall.remove(n)
                } else {
                    dfs(n, visitedSmall)
                }
            }
        }
        val visited = mutableSetOf<String>()
        if (isSmall(start)) visited.add(start)
        dfs(start, visited)
        return count
    }

    /**
     * Counts all distinct paths from 'you' to 'out' in a directed graph (AoC 2025 Day 11 Part 1).
     * Input format: "device: output1 output2 ..."
     */
    @Tool
    @LLMDescription("Counts all paths from 'you' to 'out' in a directed reactor network (AoC 2025 Day 11 Part 1). Input is lines like 'device: out1 out2'.")
    fun countReactorPaths(input: String): Int {
        val graph = parseReactorGraph(input)
        return dfsCountAllPaths(graph, "you", "out")
    }

    private fun parseReactorGraph(text: String): Map<String, List<String>> {
        if (text.isBlank()) return emptyMap()
        val norm = text.replace("\r\n", "\n").replace("\r", "\n").trimEnd('\n', '\r')
        val result = LinkedHashMap<String, MutableList<String>>()

        for (line in norm.split('\n')) {
            if (line.isBlank()) continue
            val parts = line.trim().split(':', limit = 2)
            if (parts.size != 2) continue

            val device = parts[0].trim()
            val outputs = parts[1].trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

            result.getOrPut(device) { mutableListOf() }.addAll(outputs)
        }

        return result.mapValues { it.value.toList() }
    }

    private fun dfsCountAllPaths(graph: Map<String, List<String>>, start: String, end: String): Int {
        var count = 0
        val visited = mutableSetOf<String>()

        fun dfs(node: String) {
            if (node == end) {
                count++
                return
            }

            val outputs = graph[node] ?: return
            visited.add(node)

            for (next in outputs) {
                if (next !in visited) {
                    dfs(next)
                }
            }

            visited.remove(node)
        }

        dfs(start)
        return count
    }

    /**
     * Counts paths from 'svr' to 'out' that visit both 'dac' and 'fft' (AoC 2025 Day 11 Part 2).
     * Input format: "device: output1 output2 ..."
     */
    @Tool
    @LLMDescription("Counts paths from 'svr' to 'out' that visit both 'dac' and 'fft' in any order (AoC 2025 Day 11 Part 2). Input is lines like 'device: out1 out2'.")
    fun countReactorPathsWithRequiredNodes(input: String): Long {
        val graph = parseReactorGraph(input)
        return dfsCountPathsWithRequired(graph, "svr", "out", setOf("dac", "fft"))
    }

    private fun dfsCountPathsWithRequired(
        graph: Map<String, List<String>>,
        start: String,
        end: String,
        required: Set<String>
    ): Long {
        // Optimized approach: Since it's a DAG, paths visiting both dac and fft
        // can be computed as the sum of:
        // - paths: svr -> dac -> fft -> out
        // - paths: svr -> fft -> dac -> out

        // Use memoization to count paths between nodes
        val memo = mutableMapOf<Pair<String, String>, Long>()

        fun countPaths(from: String, to: String): Long {
            val key = from to to
            if (key in memo) return memo[key]!!

            if (from == to) return 1

            var count = 0L
            val outputs = graph[from] ?: return 0

            for (next in outputs) {
                count += countPaths(next, to)
            }

            memo[key] = count
            return count
        }

        // Two orderings: dac first or fft first
        val dacFirst = countPaths(start, "dac") * countPaths("dac", "fft") * countPaths("fft", end)
        val fftFirst = countPaths(start, "fft") * countPaths("fft", "dac") * countPaths("dac", end)

        return dacFirst + fftFirst
    }
}
