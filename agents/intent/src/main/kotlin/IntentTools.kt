package fr.nicolaslinard.koog.kmp.agents.intent

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Parses an English natural language request for Advent of Code scope (year/day/part). Returns a compact JSON string with keys: year (Int), day (Int or null), part (Int or null), scope ('year'|'day').")
class IntentTools : ToolSet {

    @Tool
    @LLMDescription("Parse English intent like: 'solve day 3 of 2018' or 'solve year 2023' or 'solve day 1 of 2024 part 1' or 'solve day 1 part 2 of 2024'.")
    fun parseIntent(text: String): String {
        val t = text.trim().lowercase()
        // Try day + year + optional part (part can be before or after "of year")
        val reDayPartYear = Regex(".*day\\s+(\\d{1,2})(?:\\s+part\\s+(\\d))?.*of\\s*(\\d{4}).*")
        val reDayYearPart = Regex(".*day\\s+(\\d{1,2}).*of\\s*(\\d{4})(?:.*part\\s*(\\d))?.*")
        val reYear = Regex(".*year\\s*(\\d{4}).*")

        // First try: day X part Y of YYYY
        reDayPartYear.matchEntire(t)?.let { m ->
            val day = m.groupValues.getOrNull(1)?.toIntOrNull()
            val part = m.groupValues.getOrNull(2)?.toIntOrNull()
            val year = m.groupValues.getOrNull(3)?.toIntOrNull()
            if (year != null && day != null) {
                return "{" +
                        "\"year\":$year," +
                        "\"day\":$day," +
                        "\"part\":${part?.toString() ?: "null"}," +
                        "\"scope\":\"day\"" +
                        "}"
            }
        }

        // Second try: day X of YYYY part Z
        reDayYearPart.matchEntire(t)?.let { m ->
            val day = m.groupValues.getOrNull(1)?.toIntOrNull()
            val year = m.groupValues.getOrNull(2)?.toIntOrNull()
            val part = m.groupValues.getOrNull(3)?.toIntOrNull()
            if (year != null && day != null) {
                return "{" +
                        "\"year\":$year," +
                        "\"day\":$day," +
                        "\"part\":${part?.toString() ?: "null"}," +
                        "\"scope\":\"day\"" +
                        "}"
            }
        }

        reYear.matchEntire(t)?.let { m ->
            val year = m.groupValues.getOrNull(1)?.toIntOrNull()
            if (year != null) {
                return "{" +
                        "\"year\":$year," +
                        "\"day\":null," +
                        "\"part\":null," +
                        "\"scope\":\"year\"" +
                        "}"
            }
        }

        // Fallback: attempt to extract any year and day tokens
        val anyYear = Regex("(\\d{4})").find(t)?.groupValues?.getOrNull(1)?.toIntOrNull()
        val anyDay = Regex("\\b(\\d{1,2})\\b").find(t)?.groupValues?.getOrNull(1)?.toIntOrNull()
        val json = StringBuilder("{")
        json.append("\"year\":").append(anyYear ?: "null").append(',')
        json.append("\"day\":").append(anyDay ?: "null").append(',')
        json.append("\"part\":null,")
        json.append("\"scope\":\"").append(if (anyDay != null) "day" else if (anyYear != null) "year" else "unknown").append("\"}")
        return json.toString()
    }
}
