package com.nobos.moneymap.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nobos.moneymap.R
import com.nobos.moneymap.adapters.MonthAdapter
import com.nobos.moneymap.firebase.LoginActivity
import com.nobos.moneymap.models.Budget
import com.nobos.moneymap.viewModels.SummaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class SummaryFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var totalIncomeEditText: EditText
    private lateinit var foodExpenseEditText: EditText
    private lateinit var gasExpenseEditText: EditText
    private lateinit var entertainmentExpenseEditText: EditText
    private lateinit var savingsEditText: EditText
    private lateinit var database: FirebaseDatabase
    private lateinit var summaryViewModel: SummaryViewModel
    private lateinit var selectDateButton: Button
    private var isDialogOpen = false

    // Date picker for the Data Snapshots
    private var selectedDay: Int = 1
    private var selectedMonth: Int = 0
    private var selectedYear: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        summaryViewModel = ViewModelProvider(this)[SummaryViewModel::class.java]

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val monthAbbreviations = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        val monthAdapter = MonthAdapter(monthAbbreviations) { monthNumber, currentYear ->
            // Check if a BottomSheetDialog is already open
            if (!isDialogOpen) {
                // Set the flag to true to prevent other BottomSheetDialogs from opening
                isDialogOpen = true

                // Create a new instance of ChartsFragment with the selected month and current year
                val chartsFragment = ChartsFragment.newInstance(monthNumber, currentYear)

                // Inflate the ChartsFragment view without attaching it to the container
                val chartsView = chartsFragment.inflateView(layoutInflater, view as ViewGroup?)

                // Create and show the BottomSheetDialog with the chartsView
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                if (chartsView != null) {
                    bottomSheetDialog.setContentView(chartsView)
                } else {
                    Toast.makeText(requireContext(), "No data to present", Toast.LENGTH_SHORT).show()
                }

                // Set an OnDismissListener to the BottomSheetDialog to set the flag back to false
                bottomSheetDialog.setOnDismissListener {
                    isDialogOpen = false
                }

                bottomSheetDialog.show()
            }
        }

        // Initialize the RecyclerView and set its adapter
        val monthRecyclerView: RecyclerView = view.findViewById(R.id.monthsRecyclerView)
        monthRecyclerView.adapter = monthAdapter




        // Retrieve the views from the fragment view
        val signOutButton: Button = view.findViewById(R.id.signOutButton)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        totalIncomeEditText = view.findViewById(R.id.incomeEditText)
        foodExpenseEditText = view.findViewById(R.id.foodExpenseEditText)
        gasExpenseEditText = view.findViewById(R.id.gasExpenseEditText)
        entertainmentExpenseEditText = view.findViewById(R.id.entertainmentExpenseEditText)
        savingsEditText = view.findViewById(R.id.savingsEditText)
        selectDateButton = view.findViewById(R.id.selectDateButton)

        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }


        mAuth.currentUser?.uid ?: ""
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        calendar.apply {
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
        }.timeInMillis


        // Set an OnClickListener to the sign out button
        signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        saveButton.setOnClickListener {
            val income = totalIncomeEditText.text.toString().toIntOrNull() ?: 0
            val foodExpense = foodExpenseEditText.text.toString().toIntOrNull() ?: 0
            val gasExpense = gasExpenseEditText.text.toString().toIntOrNull() ?: 0
            val entertainmentExpense =
                entertainmentExpenseEditText.text.toString().toIntOrNull() ?: 0
            val savings = savingsEditText.text.toString().toIntOrNull() ?: 0

            // Create a new budget object
            val budget = Budget(
                userId = mAuth.currentUser?.uid ?: "",
                income = income,
                foodExpense = foodExpense,
                gasExpense = gasExpense,
                entertainmentExpense = entertainmentExpense,
                savings = savings,
                day = selectedDay,
                month = selectedMonth,
                year = selectedYear,
                timestamp = System.currentTimeMillis()
            )

            // Save the budget using the ViewModel
            summaryViewModel.saveBudget(budget)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Budget saved", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to save budget", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        // Add the same TextWatcher to each EditText field
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateTotals()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        totalIncomeEditText.addTextChangedListener(textWatcher)
        foodExpenseEditText.addTextChangedListener(textWatcher)
        gasExpenseEditText.addTextChangedListener(textWatcher)
        entertainmentExpenseEditText.addTextChangedListener(textWatcher)
        savingsEditText.addTextChangedListener(textWatcher)


    }



    private fun showDatePickerDialog() {
        val datePickerDialog =
            LayoutInflater.from(requireContext()).inflate(R.layout.date_picker_dialog, null)
        val datePicker = datePickerDialog.findViewById<DatePicker>(R.id.datePickerDialog)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(datePickerDialog)
            .setPositiveButton("OK") { _, _ ->
                // Retrieve the selected date values
                selectedDay = datePicker.dayOfMonth
                selectedMonth = datePicker.month
                selectedYear = datePicker.year

                // Use the selected date values according to your app's requirements
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    fun showChartsForMonth(month: Int, year: Int) {
        val chartsFragment = ChartsFragment.newInstance(month, year)
        val chartsView = chartsFragment.inflateView(layoutInflater, view as ViewGroup?)

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        if (chartsView != null) {
            bottomSheetDialog.setContentView(chartsView)
        }
        bottomSheetDialog.show()
    }


    private fun updateTotals() {
        val income = totalIncomeEditText.text.toString().toIntOrNull() ?: 0
        val foodExpense = foodExpenseEditText.text.toString().toIntOrNull() ?: 0
        val gasExpense = gasExpenseEditText.text.toString().toIntOrNull() ?: 0
        val entertainmentExpense = entertainmentExpenseEditText.text.toString().toIntOrNull() ?: 0
        val savings = savingsEditText.text.toString().toIntOrNull() ?: 0

        // Launch a coroutine to calculate the remaining balance
        CoroutineScope(Dispatchers.Main).launch {
            val remainingBalance = withContext(Dispatchers.Default) {
                income - foodExpense - gasExpense - entertainmentExpense - savings
            }

            // Update the income and remaining balance TextView
            val remainingBalanceTextView: TextView =
                requireView().findViewById(R.id.remainingBalanceTextView)
            remainingBalanceTextView.text = remainingBalance.toString()

            // Change the color of the remaining balance based on whether it's positive or negative
            val color = if (remainingBalance >= 0) R.color.posGreen else R.color.red
            val colorValue = ContextCompat.getColor(requireContext(), color)
            remainingBalanceTextView.setTextColor(colorValue)

            val totalIncomeTextView: TextView = requireView().findViewById(R.id.totalIncomeTextView)
            totalIncomeTextView.text = income.toString()
        }
    }
}