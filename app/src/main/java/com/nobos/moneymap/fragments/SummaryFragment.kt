package com.nobos.moneymap.fragments

import Day
import Month
import UserData
import Year
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nobos.moneymap.R
import com.nobos.moneymap.adapters.MonthAdapter
import com.nobos.moneymap.firebase.LoginActivity
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

        // Initialize the RecyclerView and set its adapter
        val monthRecyclerView: RecyclerView = view.findViewById(R.id.monthsRecyclerView)

        val monthAdapter = MonthAdapter(monthAbbreviations) { monthNumber, currentYear ->
            // Handle month click here
            // For example, you can call the showChartsForMonth() function:
            showChartsForMonth(monthNumber - 1, currentYear)
        }

        monthRecyclerView.adapter = monthAdapter

        mAuth.currentUser?.let { user ->
            val userId = user.uid
            val databaseReference = database.reference.child("Budget").child(userId)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val fetchedYears = dataSnapshot.children.mapNotNull { snapshot ->
                        snapshot.getValue(Year::class.java)
                    }
                    Log.d("SummaryFragment", "Fetched years: $fetchedYears")

                    // Call the showChartsForMonth() function with the current month and year
                    showChartsForMonth(selectedMonth, selectedYear)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("SummaryFragment", "Failed to fetch data", databaseError.toException())
                }
            })
        }


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

        mAuth.currentUser?.let { user ->
            val userId = user.uid
            val databaseReference = database.reference.child("Budget").child(userId)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val fetchedYears = dataSnapshot.children.mapNotNull { snapshot ->
                        snapshot.getValue(Year::class.java)
                    }
                    Log.d("SummaryFragment", "Fetched years: $fetchedYears")

                    // Call the showChartsForMonth() function with the current month and year
                    showChartsForMonth(selectedMonth, selectedYear)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("SummaryFragment", "Failed to fetch data", databaseError.toException())
                }
            })
        }

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
            val userId = mAuth.currentUser?.uid ?: ""
            val income = totalIncomeEditText.text.toString().toIntOrNull() ?: 0
            val foodExpense = foodExpenseEditText.text.toString().toIntOrNull() ?: 0
            val gasExpense = gasExpenseEditText.text.toString().toIntOrNull() ?: 0
            val entertainmentExpense = entertainmentExpenseEditText.text.toString().toIntOrNull() ?: 0
            val savings = savingsEditText.text.toString().toIntOrNull() ?: 0

            // Create a new UserData object
            val userData = UserData(
                income = income,
                foodExpense = foodExpense.toLong(),
                gasExpense = gasExpense.toLong(),
                entertainmentExpense = entertainmentExpense.toLong(),
                savings = savings.toLong(),
                timestamp = System.currentTimeMillis()
            )

            // Create a Day object with the UserData
            val day = Day(userKeys = mapOf(userId to userData))

            // Create a Month object with the Day object
            val month = Month(days = mapOf(selectedDay.toString() to day))

            // Create a Year object with the Month object
            val year = Year(months = mapOf(selectedMonth.toString() to month))


            // Save the UserData using the ViewModel
            summaryViewModel.saveUserData(userId, selectedYear, selectedMonth, selectedDay, userData)
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
    // Add this function in SummaryFragment class
    fun showChartsForMonth(month: Int, year: Int) {
        val chartsFragment = ChartsFragment.newInstance(month.toLong(), year)

        val chartsFragmentContainer = view?.findViewById<FrameLayout>(R.id.bottomSheetContainer)


        if (chartsFragmentContainer != null) {
            chartsFragmentContainer.setBackgroundColor(Color.TRANSPARENT)
            chartsFragmentContainer.visibility = View.GONE

            val behavior = BottomSheetBehavior.from(chartsFragmentContainer)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        childFragmentManager.beginTransaction()
                            .remove(chartsFragment)
                            .commit()
                        chartsFragmentContainer.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

            chartsFragmentContainer.setBackgroundColor(Color.TRANSPARENT)
            childFragmentManager.beginTransaction()
                .replace(R.id.bottomSheetContainer, chartsFragment)
                .commitAllowingStateLoss()
        } else {
            Toast.makeText(requireContext(), "No data to present", Toast.LENGTH_SHORT).show()
        }
    }
}