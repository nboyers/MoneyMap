data class Year (
    val months: Map<String, Month> = emptyMap()
)

data class Month(
    val days: Map<String, Day> = emptyMap()
)

data class Day(
    val userKeys: Map<String, UserData> = emptyMap()
)

data class UserData(
    val entertainmentExpense: Int = 0,
    val foodExpense: Int = 0,
    val gasExpense: Int = 0,
    val income: Int = 0,
    val savings: Int = 0,
    val timestamp: Long = 0

)