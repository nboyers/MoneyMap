package com.nobos.moneymap.firebase

// Import the necessary libraries and modules
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nobos.moneymap.MainActivity
import com.nobos.moneymap.R

class LoginActivity : AppCompatActivity() {

    // Declare FirebaseAuth instance
    private lateinit var mAuth: FirebaseAuth

    // Declare EditText, Button, and TextView views
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the activity
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        // Assign EditText, Button, and TextView views to their respective variables
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)

        // Set an OnClickListener for the login button
        loginButton.setOnClickListener {

            // Get the email and password from their respective EditText views
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if the email and password fields are empty, and display a toast message if they are
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt to sign in with the given email and password
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If the sign-in is successful, go to the MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If the sign-in fails, display a toast message
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        forgotPasswordTextView.setOnClickListener {
            val email = emailEditText.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Reset email sent. Please check your inbox.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error sending reset email. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}