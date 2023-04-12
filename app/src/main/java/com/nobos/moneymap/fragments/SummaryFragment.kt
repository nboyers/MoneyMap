package com.nobos.moneymap.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nobos.moneymap.R
import com.nobos.moneymap.adapters.DailyExpenseAdapter
import com.nobos.moneymap.firebase.LoginActivity
import com.nobos.moneymap.formating.RecyclerViewBorder
import com.nobos.moneymap.models.budget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class SummaryFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var totalIncomeEditText: EditText
    private lateinit var foodExpenseEditText: EditText
    private lateinit var gasExpenseEditText: EditText
    private lateinit var entertainmentExpenseEditText: EditText
    private lateinit var savingsEditText: EditText
    private lateinit var periodSpinner: Spinner
    private lateinit var database: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        // Retrieve the views from the fragment view
        totalIncomeEditText = view.findViewById(R.id.incomeEditText)
        foodExpenseEditText = view.findViewById(R.id.foodExpenseEditText)
        gasExpenseEditText = view.findViewById(R.id.gasExpenseEditText)
        entertainmentExpenseEditText = view.findViewById(R.id.entertainmentExpenseEditText)
        savingsEditText = view.findViewById(R.id.savingsEditText)
        periodSpinner = view.findViewById(R.id.periodSpinner)

        // Initialize the RecyclerView and its adapter
        val dailyExpenseRecyclerView: RecyclerView = view.findViewById(R.id.dailyExpenseRecyclerView)
        val dailyExpenseAdapter = DailyExpenseAdapter(emptyList()) { budget ->
            //TODO: Update chartsView with the data from the clicked budget

        }

        // Add a border around the RecyclerView
        val borderSize = resources.getDimension(R.dimen.recycler_view_border_size)
        val borderColor = ContextCompat.getColor(requireContext(), R.color.black)
        dailyExpenseRecyclerView.addItemDecoration(RecyclerViewBorder(borderSize, borderColor))

        dailyExpenseRecyclerView.adapter = dailyExpenseAdapter

        // Fetch the data for the entire month and update the RecyclerView
        val userId = mAuth.currentUser?.uid ?: ""
        val budgetRef = database.getReference("Budget").child(userId)
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

        budgetRef.orderByChild("timestamp")
            .startAt(monthStart.toDouble())
            .endAt(monthEnd.toDouble())
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val budgets = snapshot.children.mapNotNull { it.getValue(budget::class.java) }
                    dailyExpenseAdapter.updateDailyExpenses(budgets)
                    dailyExpenseRecyclerView.visibility = if (budgets.isNotEmpty()) View.VISIBLE else View.GONE
                }


                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        // Initialize UI components and set up listeners and data fetching
        val signOutButton: Button = view.findViewById(R.id.signOutButton)

        // Set an OnClickListener to the sign out button
        signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        val saveButton: Button = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val income = totalIncomeEditText.text.toString().toIntOrNull() ?: 0
            val foodExpense = foodExpenseEditText.text.toString().toIntOrNull() ?: 0
            val gasExpense = gasExpenseEditText.text.toString().toIntOrNull() ?: 0
            val entertainmentExpense =
                entertainmentExpenseEditText.text.toString().toIntOrNull() ?: 0
            val savings = savingsEditText.text.toString().toIntOrNull() ?: 0
            val periodType = periodSpinner.selectedItem.toString()

            // Create a new budget object
            val budget = budget(
                userId = mAuth.currentUser?.uid ?: "",
                income = income,
                foodExpense = foodExpense,
                gasExpense = gasExpense,
                entertainmentExpense = entertainmentExpense,
                savings = savings,
                periodType = periodType,
                timestamp = System.currentTimeMillis()
            )
            // Generate a new key for the budget object
            val newBudgetRef = database.getReference("Budget")
            val newUserId = mAuth.currentUser?.uid ?: ""

            // Add the budget object to the database
            newBudgetRef.child(newUserId).setValue(budget)
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
            val remainingBalanceTextView: TextView = requireView().findViewById(R.id.remainingBalanceTextView)
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