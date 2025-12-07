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
}
