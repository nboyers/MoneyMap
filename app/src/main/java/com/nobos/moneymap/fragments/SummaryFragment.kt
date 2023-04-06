package com.nobos.moneymap.fragments
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nobos.moneymap.R
import android.widget.Button
import com.nobos.moneymap.firebase.LoginActivity
import com.nobos.moneymap.models.Budget
import android.widget.Toast

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

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Move the relevant code from MainActivity to here
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
            val income = totalIncomeTextView.text.toString().toIntOrNull() ?: 0
            val foodExpense = foodExpenseEditText.text.toString().toIntOrNull() ?: 0
            val gasExpense = gasExpenseEditText.text.toString().toIntOrNull() ?: 0
            val entertainmentExpense = entertainmentExpenseEditText.text.toString().toIntOrNull() ?: 0
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
                periodType = periodType
            )

            // Generate a new key for the budget object
            val budgetKey = budgetRef.push().key ?: ""

            // Add the budget object to the database
            budgetRef.child(budgetKey).setValue(budget)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Budget saved", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to save budget", Toast.LENGTH_SHORT)
                        .show()
                }
        }


    }
}
