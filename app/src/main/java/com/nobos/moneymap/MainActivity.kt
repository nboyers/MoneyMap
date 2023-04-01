package com.nobos.moneymap
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if the user is logged in, otherwise navigate to LoginActivity
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }
        }




        // Initialize UI components
        val totalIncomeTextView: TextView = findViewById(R.id.totalIncomeTextView)
        val remainingBalanceTextView: TextView = findViewById(R.id.remainingBalanceTextView)
        val incomeEditText: EditText = findViewById(R.id.incomeEditText)
        val foodExpenseEditText: EditText = findViewById(R.id.foodExpenseEditText)
        val gasExpenseEditText: EditText = findViewById(R.id.gasExpenseEditText)
        val entertainmentExpenseEditText: EditText = findViewById(R.id.entertainmentExpenseEditText)
        val savingsEditText: EditText = findViewById(R.id.savingsEditText)
        val saveButton: Button = findViewById(R.id.saveButton)
        val signOutButton: Button = findViewById(R.id.signOutButton)

        // Set up listeners
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
                    savings = savings ?: 0
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

        // Fetch data from Firestore and update UI
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("budgets").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val budget = document.toObject(Budget::class.java)

                        budget?.let { b ->
                            totalIncomeTextView.text = b.income.toString()
                            val remainingBalance = b.income - b.foodExpense - b.gasExpense - b.entertainmentExpense - b.savings
                            remainingBalanceTextView.text = remainingBalance.toString()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }

        // Sign out
        signOutButton.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
