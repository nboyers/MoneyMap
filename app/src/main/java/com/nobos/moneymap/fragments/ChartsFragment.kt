package com.nobos.moneymap.fragments

import UserData
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
import com.google.firebase.database.FirebaseDatabase
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

        // Generating Data
        val sampleData = generateSampleData()

        setupPieChart(
            pieChart,
        sampleData.income,
        sampleData.foodExpense,
        sampleData.gasExpense,
        sampleData.entertainmentExpense,
        sampleData.savings
        )

        return view
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
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(14f)
            setValueTextColor(Color.BLACK)
        }

        chart.apply {
            this.data = data
            description.isEnabled = false
            legend.isEnabled = false
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(14f)
            setHoleColor(Color.TRANSPARENT)
            animateY(1000)
        }
    }

    private fun generateSampleData(): UserData {
        return UserData(
            timestamp = Calendar.getInstance().apply {
                set(Calendar.YEAR, get(Calendar.YEAR))
                set(Calendar.MONTH, get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, 1)
            }.timeInMillis,
            income = (2000..5000).random(),
            foodExpense = (500..1500).random(),
            gasExpense = (100..500).random(),
            entertainmentExpense = (300..800).random(),
            savings = (500..2000).random()
        )
    }

}