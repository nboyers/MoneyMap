package com.nobos.moneymap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.utils.ColorTemplate


class ChartsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)

        // Get the chart views from the layout
        val barChart: BarChart = findViewById(R.id.barChart)
        val lineChart: LineChart = findViewById(R.id.lineChart)
        val pieChart: PieChart = findViewById(R.id.pieChart)

        // Get the data from the intent extras
        val income = intent.getIntExtra("income", 0)
        val foodExpense = intent.getIntExtra("foodExpense", 0)
        val gasExpense = intent.getIntExtra("gasExpense", 0)
        val entertainmentExpense = intent.getIntExtra("entertainmentExpense", 0)
        val savings = intent.getIntExtra("savings", 0)

        // Set up the charts with the data
        setupBarChart(barChart, income, foodExpense, gasExpense, entertainmentExpense, savings)
        setupLineChart(lineChart, income, foodExpense, gasExpense, entertainmentExpense, savings)
        setupPieChart(pieChart, income, foodExpense, gasExpense, entertainmentExpense, savings)
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
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.teal_700))

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

    private fun setupPieChart(pieChart: PieChart,
                              income: Int,
                              foodExpense: Int,
                              gasExpense: Int,
                              entertainmentExpense: Int,
                              savings: Int) {
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
            ContextCompat.getColor(this, R.color.teal_700),
            ContextCompat.getColor(this, R.color.purple_200),
            ContextCompat.getColor(this, R.color.orange),
            ContextCompat.getColor(this, R.color.red),
            ContextCompat.getColor(this, R.color.green)
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


