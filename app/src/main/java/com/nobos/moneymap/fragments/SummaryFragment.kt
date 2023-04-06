package com.nobos.moneymap.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nobos.moneymap.R
import com.nobos.moneymap.firebase.LoginActivity
import com.nobos.moneymap.models.Budget

class SummaryFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        val totalIncomeTextView: TextView = view.findViewById(R.id.totalIncomeTextView)
        val remainingBalanceTextView: TextView = view.findViewById(R.id.remainingBalanceTextView)
        val incomeEditText: EditText = view.findViewById(R.id.incomeEditText)
        val foodExpenseEditText: EditText = view.findViewById(R.id.foodExpenseEditText)
        val gasExpenseEditText: EditText = view.findViewById(R.id.gasExpenseEditText)
        val entertainmentExpenseEditText: EditText =
            view.findViewById(R.id.entertainmentExpenseEditText)
        val savingsEditText: EditText = view.findViewById(R.id.savingsEditText)
        val saveButton: Button = view.findViewById(R.id.saveButton)
        val signOutButton: Button = view.findViewById(R.id.signOutButton)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if the user is logged in, otherwise navigate to LoginActivity
        val currentUser = mAuth.currentUser

        if (currentUser == null) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        } else if (!currentUser.isEmailVerified) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        saveButton.setOnClickListener {
            val income = incomeEditText.text.toString().toIntOrNull()
            val foodExpense = foodExpenseEditText.text.toString().toIntOrNull()
            val gasExpense = gasExpenseEditText.text.toString().toIntOrNull()
            val entertainmentExpense = entertainmentExpenseEditText.text.toString().toIntOrNull()
            val savings = savingsEditText.text.toString().toIntOrNull()

            currentUser?.let { user ->
                val userId = user.uid
                val budget = Budget(
                    id = userId,
                    userId = userId,
                    income = income ?: 0,
                    foodExpense = foodExpense ?: 0,
                    gasExpense = gasExpense ?: 0,
                    entertainmentExpense = entertainmentExpense ?: 0,
                    savings = savings ?: 0,
                    timestamp = System.currentTimeMillis()
                )

                db.collection("budgets").document(userId).set(budget)
                    .addOnSuccessListener {
                        // Handle success
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        e.stackTraceToString()
                    }
            }
        }

        // Fetch data from Realtime database and update UI
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("budgets").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val budget = document.toObject(Budget::class.java)

                        budget?.let { b ->
                            totalIncomeTextView.text = b.income.toString()
                            val remainingBalance =
                                b.income - b.foodExpense - b.gasExpense - b.entertainmentExpense - b.savings
                            remainingBalanceTextView.text = remainingBalance.toString()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    e.stackTraceToString()
                }
        }

        // Set an OnClickListener to the sign out button
        // Sign out
        signOutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Set an OnClickListener to the sign out button
        signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}