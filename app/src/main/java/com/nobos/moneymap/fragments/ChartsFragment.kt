import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nobos.moneymap.R
import com.nobos.moneymap.models.Budget
import java.util.*


class ChartsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    companion object {
        private const val ARG_SELECTED_MONTH = "selected_month"
        private const val ARG_SELECTED_YEAR = "selected_year"

        @JvmStatic
        fun newInstance(selectedMonth: Int, selectedYear: Int) =
            ChartsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SELECTED_MONTH, selectedMonth)
                    putInt(ARG_SELECTED_YEAR, selectedYear)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the chart views from the layout
        val barChart: BarChart = view.findViewById(R.id.barChart)
        val lineChart: LineChart = view.findViewById(R.id.lineChart)
        val pieChart: PieChart = view.findViewById(R.id.pieChart)

        // Retrieve the selected month and year from the arguments bundle
        val selectedMonth = arguments?.getInt(ARG_SELECTED_MONTH) ?: 1
        val selectedYear = arguments?.getInt(ARG_SELECTED_YEAR) ?: 2022

        // Fetch data for the selected month and year
        fetchUserData { budgets ->
            val selectedMonthBudget = budgets
                .filter { budget -> budget.periodType == "monthly" }
                .filter { budget -> budget.month == selectedMonth && budget.year == selectedYear }

            // Aggregate the data for the selected month and year
            val totalIncome = selectedMonthBudget.sumOf { it.income }
            val totalFoodExpense = selectedMonthBudget.sumOf { it.foodExpense }
            val totalGasExpense = selectedMonthBudget.sumOf { it.gasExpense }
            val totalEntertainmentExpense = selectedMonthBudget.sumOf { it.entertainmentExpense }
            val totalSavings = selectedMonthBudget.sumOf { it.savings }

            // Set up and populate the charts with the data
            setupBarChart(barChart, totalIncome, totalFoodExpense, totalGasExpense, totalEntertainmentExpense, totalSavings)
            setupLineChart(lineChart, totalIncome, totalFoodExpense, totalGasExpense, totalEntertainmentExpense, totalSavings)
            setupPieChart(pieChart, totalIncome, totalFoodExpense, totalGasExpense, totalEntertainmentExpense, totalSavings)
        }
    }


    private fun fetchUserData(onSuccess: (List<Budget>) -> Unit) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            val userId = user.uid

            db.reference.child("budgets").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val budgets =
                            snapshot.children.mapNotNull { it.getValue(Budget::class.java) }
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
}