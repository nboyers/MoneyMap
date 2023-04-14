package com.nobos.moneymap.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nobos.moneymap.R
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
    private lateinit var periodSpinner: Spinner
    private lateinit var database: FirebaseDatabase
    private lateinit var summaryViewModel: SummaryViewModel

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


        val monthContainer: RecyclerView = view.findViewById(R.id.monthRecyclerView)

        // Set a horizontal LinearLayoutManager for the monthContainer
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        monthContainer.layoutManager = layoutManager

        val userCalendar = Calendar.getInstance()
        val currentYear = userCalendar.get(Calendar.YEAR)


        val monthAbbreviations = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        // Initialize the monthlyExpenseAdapter
        val monthsContainer: LinearLayout = view.findViewById(R.id.monthsContainer)

        for (month in monthAbbreviations) {
            val monthView = createMonthView(month)

            monthView.setOnClickListener {
                //TODO: Handle month view click event
            }
            monthsContainer.addView(monthView)
        }




        // Retrieve the views from the fragment view
        val signOutButton: Button = view.findViewById(R.id.signOutButton)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        totalIncomeEditText = view.findViewById(R.id.incomeEditText)
        foodExpenseEditText = view.findViewById(R.id.foodExpenseEditText)
        gasExpenseEditText = view.findViewById(R.id.gasExpenseEditText)
        entertainmentExpenseEditText = view.findViewById(R.id.entertainmentExpenseEditText)
        savingsEditText = view.findViewById(R.id.savingsEditText)
        val selectDateButton: Button = view.findViewById(R.id.selectDateButton)
        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }


        val userId = mAuth.currentUser?.uid ?: ""
        val calendar = Calendar.getInstance()
        val monthStart = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val monthEnd = calendar.apply {
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
            val periodType = periodSpinner.selectedItem.toString()

            // Create a new budget object
            val budget = Budget(
                userId = mAuth.currentUser?.uid ?: "",
                income = income,
                foodExpense = foodExpense,
                gasExpense = gasExpense,
                entertainmentExpense = entertainmentExpense,
                savings = savings,
                periodType = periodType,
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
                val day = datePicker.dayOfMonth
                val month = datePicker.month
                val year = datePicker.year

                // Use the selected date values according to your app's requirements
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    private fun createMonthView(monthText: String): TextView {
        val monthView = TextView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 8, 8, 8)
        monthView.layoutParams = layoutParams
        monthView.gravity = Gravity.CENTER
        monthView.text = monthText
        monthView.setTextColor(Color.WHITE)
        monthView.setBackgroundResource(R.drawable.square_month_background) // You'll need to create this background
        monthView.setPadding(16, 16, 16, 16)
        return monthView
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