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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    // Declare FirebaseAuth instance
    private lateinit var mAuth: FirebaseAuth

    // Declare views using Kotlin Android Extensions
    private val emailEditText by lazy { findViewById<EditText>(R.id.emailEditText) }
    private val passwordEditText by lazy { findViewById<EditText>(R.id.passwordEditText) }
    private val loginButton by lazy { findViewById<Button>(R.id.loginButton) }
    private val forgotPasswordTextView by lazy { findViewById<TextView>(R.id.forgotPasswordTextView) }
    private val signupButton by lazy { findViewById<Button>(R.id.signUpButton) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the activity
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        // Set an OnClickListener for the buttons
        loginButton.setOnClickListener { signIn() }
        signupButton.setOnClickListener { signUp() }
        forgotPasswordTextView.setOnClickListener { resetPassword() }
    }

    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }


    private fun signIn() = CoroutineScope(Dispatchers.Main).launch {

        // Get the email and password from their respective EditText views
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Check if the email and password fields are empty, and display a toast message if they are
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this@LoginActivity, "Please fill in all fields.", Toast.LENGTH_SHORT)
                .show()
            return@launch
        }

        // Run the sign-in process on a background thread using coroutines to improve performance
        withContext(Dispatchers.IO) {
            // Attempt to sign in with the given email and password
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If the sign-in is successful, go to the MainActivity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // If the sign-in fails, display a toast message
                        Toast.makeText(
                            this@LoginActivity,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun resetPassword() {
        val email = emailEditText.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
            return
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
