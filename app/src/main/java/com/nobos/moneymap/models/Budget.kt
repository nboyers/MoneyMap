data class Budget(
    val years: Map<String, Year> = emptyMap()
)

data class Year (
    val months: Map<String, Month> = emptyMap()
)

data class Month(
    val days: Map<String, Day> = emptyMap()
)

data class Day(
    val userKeys: Map<String, UserData> = emptyMap()
)

data class UserData (
    val entertainmentExpense: Long = 0,
    val foodExpense: Long = 0,
    val gasExpense: Long = 0,
    val income: Int = 0,
    val savings: Long = 0,
    val timestamp: Long = 0

)
