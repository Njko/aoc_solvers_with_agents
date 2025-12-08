fun parseIntent(text: String): String {
    val t = text.trim().lowercase()
    // Try day + year + optional part
    val reDayYearPart = Regex(".*day\\s+(\\d{1,2}).*of\\s*(\\d{4})(?:.*part\\s*(\\d))?.*")
    val reYear = Regex(".*year\\s*(\\d{4}).*")

    reDayYearPart.matchEntire(t)?.let { m ->
        val day = m.groupValues.getOrNull(1)?.toIntOrNull()
        val year = m.groupValues.getOrNull(2)?.toIntOrNull()
        val part = m.groupValues.getOrNull(3)?.toIntOrNull()
        println("Matched: day=$day, year=$year, part=$part")
        if (year != null && day != null) {
            return "{" +
                    "\"year\":$year," +
                    "\"day\":$day," +
                    "\"part\":${part?.toString() ?: "null"}," +
                    "\"scope\":\"day\"" +
                    "}"
        }
    }

    println("No match with reDayYearPart")

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

    // Fallback
    val anyYear = Regex("(\\d{4})").find(t)?.groupValues?.getOrNull(1)?.toIntOrNull()
    val anyDay = Regex("\\b(\\d{1,2})\\b").find(t)?.groupValues?.getOrNull(1)?.toIntOrNull()
    println("Fallback: anyYear=$anyYear, anyDay=$anyDay")
    val json = StringBuilder("{")
    json.append("\"year\":").append(anyYear ?: "null").append(',')
    json.append("\"day\":").append(anyDay ?: "null").append(',')
    json.append("\"part\":null,")
    json.append("\"scope\":\"").append(if (anyDay != null) "day" else if (anyYear != null) "year" else "unknown").append("\"}")
    return json.toString()
}

println("Test 1: 'solve day 8 of 2025'")
println(parseIntent("solve day 8 of 2025"))
println()

println("Test 2: 'solve day 7 of 2025'")
println(parseIntent("solve day 7 of 2025"))
println()

println("Test 3: 'solve day 1 of 2023'")
println(parseIntent("solve day 1 of 2023"))
