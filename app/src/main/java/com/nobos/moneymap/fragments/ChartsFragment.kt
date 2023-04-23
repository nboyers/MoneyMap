package com.nobos.moneymap.fragments

import Budget
import Day
import Month
import UserData
import Year
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nobos.moneymap.R
import java.util.Calendar

//FIXME: Fix retrieval of the database to app to Graph
class ChartsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    companion object {
        private const val ARG_SELECTED_MONTH = "selected_month"
        private const val ARG_SELECTED_YEAR = "selected_year"
        fun newInstance(
            selectedMonth: Long = 1,
            selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        ): ChartsFragment {
            val fragment = ChartsFragment()
            val args = Bundle()
            args.putInt(ARG_SELECTED_MONTH, selectedMonth.toInt())
            args.putInt(ARG_SELECTED_YEAR, selectedYear)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ChartsFragment", "PieEntries:")
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_charts, container, false)
        // Get the chart views from the layout
        val pieChart: PieChart = view.findViewById(R.id.pieChart)

        // Retrieve the selected month and year from the arguments bundle
        val selectedMonth = arguments?.getInt(ARG_SELECTED_MONTH)?.toString() ?: Calendar.getInstance().get(Calendar.MONTH).toString()
        val selectedYear = arguments?.getInt(ARG_SELECTED_YEAR)?.toString() ?: Calendar.getInstance().get(Calendar.YEAR).toString()

        // Fetch data for the selected month and year
        fetchUserData(
            selectedMonth,
            selectedYear,
            { budgets ->
            // Filter the UserData objects by day
            val selectedMonthBudget = budgets.filter { userData ->
                userData.timestamp.toCalendar().get(Calendar.MONTH).toString() == selectedMonth &&
                        userData.timestamp.toCalendar().get(Calendar.YEAR)
                            .toString() == selectedYear
            }

            // Aggregate the data for the selected month and year
            val totalIncome = selectedMonthBudget.sumOf { it.income }
            val totalFoodExpense = selectedMonthBudget.sumOf { it.foodExpense }
            val totalGasExpense = selectedMonthBudget.sumOf { it.gasExpense }
            val totalEntertainmentExpense = selectedMonthBudget.sumOf { it.entertainmentExpense }
            val totalSavings = selectedMonthBudget.sumOf { it.savings }

            // Set up and populate the charts with the data
            setupPieChart(
                pieChart,
                totalIncome,
                totalFoodExpense.toInt(),
                totalGasExpense.toInt(),
                totalEntertainmentExpense.toInt(),
                totalSavings.toInt()
            )
        }, {
            Log.d("ChartFragment","Failed to fetch user data")
           // Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        })

        return view
    }


    private fun fetchUserData(
        selectedMonth: String,
        selectedYear: String,
        onSuccess: (List<UserData>) -> Unit,
        onFailure: (String) -> Unit
    ){
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            val userId = user.uid
            db.reference.child("Budget").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!isAdded) return
// Gets The data from the database and all the children nodes
                        if (snapshot.exists()) {
                            val userBudgetYears = mutableMapOf<String, Year>()
                            snapshot.children.forEach yearLoop@{ yearSnapshot ->
                                val yearKey = yearSnapshot.key ?: return@yearLoop
                                if (yearSnapshot.hasChildren()) {
                                    val yearMonths = mutableMapOf<String, Month>()
                                    yearSnapshot.children.forEach monthLoop@{ monthSnapshot ->
                                        val monthKey = monthSnapshot.key ?: return@monthLoop
                                        if (monthSnapshot.hasChildren()) {
                                            val monthDays = mutableMapOf<String, Day>()
                                            monthSnapshot.children.forEach dayLoop@{ daySnapshot ->
                                                val dayKey = daySnapshot.key ?: return@dayLoop
                                                if (daySnapshot.hasChildren()) {
                                                    val userKeysData = mutableMapOf<String, UserData>()
                                                    daySnapshot.children.forEach userDataLoop@{ userDataSnapshot ->
                                                        val userKey = userDataSnapshot.key ?: return@userDataLoop
                                                        val userData = userDataSnapshot.getValue(UserData::class.java) ?: return@userDataLoop
                                                        userKeysData[userKey] = userData
                                                    }
                                                    monthDays[dayKey] = Day(userKeysData)
                                                }
                                            }
                                            yearMonths[monthKey] = Month(monthDays)
                                        }
                                    }
                                    userBudgetYears[yearKey] = Year(yearMonths)
                                }
                            }
                            val userBudget = Budget(userBudgetYears)

                            Log.d("ChartsFragment", "User budget data: $userBudget")

                            // If userBudget is not null, pass the list of UserData objects to the onSuccess callback
                            userBudget.let { budget ->
                                val year = budget.years[selectedYear]
                                val month = year?.months?.get(selectedMonth)
                                val dayList = month?.days?.values?.toList()
                                val userDataList = dayList?.flatMap { it.userKeys.values }

                                if (userDataList != null && isAdded) {
                                    onSuccess(userDataList)
                                } else {
                                    Log.d("FAILED", "No data found for the selected month and year")
                                    onFailure("No data found for the selected month and year")
                                }
                            }

                        } else {
                            Log.d("ChartsFragment", "No data found for user $userId")
                            if (isAdded) {
                                Log.d("FAILED", "Error fetching data:")
                                onFailure("No data found for user")
                            }
                        }

                    }


                    override fun onCancelled(error: DatabaseError) {
                        // Handle failure
                        Log.d("FAILED", "Error fetching data: ${error.message}")
                        onFailure("Error fetching data: ${error.message}")
                    }
                })
        }
    }

    private fun setupPieChart(
        chart: PieChart,
        totalIncome: Int,
        totalFoodExpense: Int,
        totalGasExpense: Int,
        totalEntertainmentExpense: Int,
        totalSavings: Int
    ) {
        val entries = listOf(
            PieEntry(totalIncome.toFloat(), "Income"),
            PieEntry(totalFoodExpense.toFloat(), "Food"),
            PieEntry(totalGasExpense.toFloat(), "Gas"),
            PieEntry(totalEntertainmentExpense.toFloat(), "Entertainment"),
            PieEntry(totalSavings.toFloat(), "Savings")
        )

        val dataSet = PieDataSet(entries, "Monthly Expenses").apply {
            setColors(*ColorTemplate.JOYFUL_COLORS)
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(14f)
            setValueTextColor(Color.BLACK)
        }

        chart.apply {
            this.data = data
            description.isEnabled = false
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(14f)
            setHoleColor(Color.TRANSPARENT)
            animateY(1000)
        }
    }
    private fun Long.toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return calendar
    }

}
