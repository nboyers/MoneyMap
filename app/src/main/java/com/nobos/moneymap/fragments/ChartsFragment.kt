package com.nobos.moneymap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nobos.moneymap.R
import com.nobos.moneymap.models.Budget
import java.util.Calendar


class ChartsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    companion object {
        private const val ARG_SELECTED_MONTH = "selected_month"
        private const val ARG_SELECTED_YEAR = "selected_year"
        fun newInstance(selectedMonth: Int = 1, selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)): ChartsFragment {
            val fragment = ChartsFragment()
            val args = Bundle()
            args.putInt(ARG_SELECTED_MONTH, selectedMonth)
            args.putInt(ARG_SELECTED_YEAR, selectedYear)
            fragment.arguments = args
            return fragment
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
        val pieChart: PieChart = view.findViewById(R.id.pieChart)

        // Retrieve the selected month and year from the arguments bundle
        val selectedMonth = arguments?.getInt(ARG_SELECTED_MONTH) ?: Calendar.getInstance().get(Calendar.MONTH)
        val selectedYear = arguments?.getInt(ARG_SELECTED_YEAR) ?: Calendar.getInstance().get(Calendar.YEAR)


        // Fetch data for the selected month and year
        fetchUserData({ budgets ->
            val selectedMonthBudget = budgets
                .filter { budget -> budget.month == selectedMonth && budget.year == selectedYear }

            // Aggregate the data for the selected month and year
            val totalIncome = selectedMonthBudget.sumOf { it.income }
            val totalFoodExpense = selectedMonthBudget.sumOf { it.foodExpense }
            val totalGasExpense = selectedMonthBudget.sumOf { it.gasExpense }
            val totalEntertainmentExpense = selectedMonthBudget.sumOf { it.entertainmentExpense }
            val totalSavings = selectedMonthBudget.sumOf { it.savings }

            // Set up and populate the charts with the data
            setupPieChart(pieChart, totalIncome, totalFoodExpense, totalGasExpense, totalEntertainmentExpense, totalSavings)
        }, {
            Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        })



    }

    private fun fetchUserData(onSuccess: (List<Budget>) -> Unit, onFailure: () -> Unit) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            val userId = user.uid

            db.reference.child("Budgets").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val budgets =
                            snapshot.children.mapNotNull { it.getValue(Budget::class.java) }
                        onSuccess(budgets)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle failure
                        onFailure()
                    }
                })
        }
    }


    fun inflateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        return inflater.inflate(R.layout.fragment_charts, container, false)
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