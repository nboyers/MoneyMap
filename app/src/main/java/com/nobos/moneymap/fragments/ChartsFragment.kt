import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.nobos.moneymap.R
import com.nobos.moneymap.models.budget

class ChartsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_charts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the chart views from the layout
        val barChart: BarChart = view.findViewById(R.id.barChart)
        val lineChart: LineChart = view.findViewById(R.id.lineChart)
        val pieChart: PieChart = view.findViewById(R.id.pieChart)

        fetchUserData { budgets ->
            if (budgets.isNotEmpty()) {
                val averageBudget = calculateAverageBudget(
                    budgets,
                    periodType = budgets.first().periodType
                )

                // Set up the charts with the data
                setupBarChart(
                    barChart,
                    averageBudget.income,
                    averageBudget.foodExpense,
                    averageBudget.gasExpense,
                    averageBudget.entertainmentExpense,
                    averageBudget.savings
                )
                setupLineChart(
                    lineChart,
                    averageBudget.income,
                    averageBudget.foodExpense,
                    averageBudget.gasExpense,
                    averageBudget.entertainmentExpense,
                    averageBudget.savings
                )
                setupPieChart(
                    pieChart,
                    averageBudget.income,
                    averageBudget.foodExpense,
                    averageBudget.gasExpense,
                    averageBudget.entertainmentExpense,
                    averageBudget.savings
                )
            }
        }
    }


    private fun fetchUserData(onSuccess: (List<budget>) -> Unit) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            val userId = user.uid

            db.reference.child("budgets").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val budgets =
                            snapshot.children.mapNotNull { it.getValue(budget::class.java) }
                        onSuccess(budgets)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle failure
                        error.toException().printStackTrace()
                    }
                })
        }
    }


    private fun setupBarChart(
        barChart: BarChart,
        income: Int,
        foodExpense: Int,
        gasExpense: Int,
        entertainmentExpense: Int,
        savings: Int
    ) {
        // Prepare the data
        val barEntries = ArrayList<BarEntry>()
        barEntries.add(BarEntry(0f, income.toFloat()))
        barEntries.add(BarEntry(1f, foodExpense.toFloat()))
        barEntries.add(BarEntry(2f, gasExpense.toFloat()))
        barEntries.add(BarEntry(3f, entertainmentExpense.toFloat()))
        barEntries.add(BarEntry(4f, savings.toFloat()))

        // Create a BarDataSet and set colors
        val barDataSet = BarDataSet(barEntries, "Expenses")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        // Create a BarData object and set it to the chart
        val barData = BarData(barDataSet)
        barChart.data = barData

        // Customize the chart
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.xAxis.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.setDrawGridLines(false)
        barChart.animateY(1000)

        // Refresh the chart
        barChart.invalidate()
    }


    private fun setupLineChart(
        lineChart: LineChart,
        income: Int,
        foodExpense: Int,
        gasExpense: Int,
        entertainmentExpense: Int,
        savings: Int
    ) {
        // Prepare the data
        val lineEntries = ArrayList<Entry>()
        lineEntries.add(Entry(0f, income.toFloat()))
        lineEntries.add(Entry(1f, foodExpense.toFloat()))
        lineEntries.add(Entry(2f, gasExpense.toFloat()))
        lineEntries.add(Entry(3f, entertainmentExpense.toFloat()))
        lineEntries.add(Entry(4f, savings.toFloat()))

        // Create a LineDataSet and set colors
        val lineDataSet = LineDataSet(lineEntries, "Expenses")
        lineDataSet.lineWidth = 2.5f
        lineDataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.teal_700))

        // Create a LineData object and set it to the chart
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Customize the chart
        lineChart.description.isEnabled = false
        lineChart.xAxis.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.animateY(1000)

        // Refresh the chart
        lineChart.invalidate()
    }

    private fun setupPieChart(
        pieChart: PieChart,
        income: Int,
        foodExpense: Int,
        gasExpense: Int,
        entertainmentExpense: Int,
        savings: Int
    ) {
        // Prepare the data
        val pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(income.toFloat(), "Income"))
        pieEntries.add(PieEntry(foodExpense.toFloat(), "Food Expense"))
        pieEntries.add(PieEntry(gasExpense.toFloat(), "Gas Expense"))
        pieEntries.add(PieEntry(entertainmentExpense.toFloat(), "Entertainment Expense"))
        pieEntries.add(PieEntry(savings.toFloat(), "Savings"))

        // Create a PieDataSet and set colors
        val pieDataSet = PieDataSet(pieEntries, "Expenses")
        pieDataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.teal_700),
            ContextCompat.getColor(requireContext(), R.color.purple_200),
            ContextCompat.getColor(requireContext(), R.color.orange),
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.green)
        )

        // Create a PieData object and set it to the chart
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        // Customize the chart
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setUsePercentValues(true)
        pieChart.animateY(1000)

        // Refresh the chart
        pieChart.invalidate()
    }

    // Add the function to calculate the average budget or any other aggregation you want to display
    private fun calculateAverageBudget(budgets: List<budget>, periodType: String): budget {
        val totalbudgets = budgets.size

        var totalIncome = budgets.sumOf { it.income }
        var totalFoodExpense = budgets.sumOf { it.foodExpense }
        var totalGasExpense = budgets.sumOf { it.gasExpense }
        var totalEntertainmentExpense = budgets.sumOf { it.entertainmentExpense }
        var totalSavings = budgets.sumOf { it.savings }

        for (budget in budgets) {
            when (periodType) {
                "weekly" -> {
                    totalIncome += budget.income / 4
                    totalFoodExpense += budget.foodExpense / 4
                    totalGasExpense += budget.gasExpense / 4
                    totalEntertainmentExpense += budget.entertainmentExpense / 4
                    totalSavings += budget.savings / 4
                }
                "monthly" -> {
                    totalIncome += budget.income
                    totalFoodExpense += budget.foodExpense
                    totalGasExpense += budget.gasExpense
                    totalEntertainmentExpense += budget.entertainmentExpense
                    totalSavings += budget.savings
                }
                "yearly" -> {
                    totalIncome += budget.income * 12
                    totalFoodExpense += budget.foodExpense * 12
                    totalGasExpense += budget.gasExpense * 12
                    totalEntertainmentExpense += budget.entertainmentExpense * 12
                    totalSavings += budget.savings * 12
                }
            }
        }

        val averageIncome = totalIncome / totalbudgets
        val averageFoodExpense = totalFoodExpense / totalbudgets
        val averageGasExpense = totalGasExpense / totalbudgets
        val averageEntertainmentExpense = totalEntertainmentExpense / totalbudgets
        val averageSavings = totalSavings / totalbudgets

        return budget(
            income = averageIncome,
            foodExpense = averageFoodExpense,
            gasExpense = averageGasExpense,
            entertainmentExpense = averageEntertainmentExpense,
            savings = averageSavings,
            periodType = periodType // pass the periodType parameter here
        )
    }
}