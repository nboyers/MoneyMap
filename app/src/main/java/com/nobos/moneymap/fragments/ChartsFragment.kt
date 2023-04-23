package com.nobos.moneymap.fragments

import Budget
import UserData
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
import java.util.Calendar

import android.util.Log

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
        Log.d("ChartsFragment", "onViewCreated")
        // Get the chart views from the layout
        val pieChart: PieChart = view.findViewById(R.id.pieChart)

        // Retrieve the selected month and year from the arguments bundle
        val selectedMonth =
            arguments?.getInt(ARG_SELECTED_MONTH) ?: Calendar.getInstance().get(Calendar.MONTH)
        val selectedYear =
            arguments?.getInt(ARG_SELECTED_YEAR) ?: Calendar.getInstance().get(Calendar.YEAR)

        // Fetch data for the selected month and year
        fetchUserData({ budgets ->
            //FIXME: THIS IS JUST DOESN'T WORK LOL
            val selectedMonthBudget = budgets.filter { userData ->
                userData.month == selectedMonth && userData.year == selectedYear
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
            Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        })

        return view
    }

    private fun fetchUserData(onSuccess: (List<UserData>) -> Unit, onFailure: () -> Unit) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            val userId = user.uid
            db.reference.child("Budget").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val userBudget = snapshot.getValue(Budget::class.java)

                            // If userBudget is not null, pass the list of UserData objects to the onSuccess callback
                            userBudget?.let {
                                //FIXME: THIS ALSO DOESN'T WORK LOL. IDK WHY
                                val userDataList = it.userId.values.flatMap { user -> user.userKey.values }
                                if (isAdded) {
                                    onSuccess(userDataList)
                                }
                            } ?: onFailure()

                        } else {
                            Log.d("ChartsFragment", "No data found for user $userId")
                            if (isAdded) {
                                onFailure()
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle failure
                        Log.d("ChartsFragment", "Error fetching data: ${error.message}")
                        onFailure()
                    }
                })
        }
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

        Log.d("ChartsFragment", "PieEntries: $pieEntries") // Moved the log statement here

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