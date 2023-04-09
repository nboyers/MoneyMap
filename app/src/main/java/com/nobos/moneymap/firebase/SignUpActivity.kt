package com.nobos.moneymap.firebase

// SignUpActivity.kt

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nobos.moneymap.MainActivity
import com.nobos.moneymap.R


class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmPasswordEditText)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val alreadyRegisteredButton: Button = findViewById(R.id.alreadyRegisteredButton)

        alreadyRegisteredButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (password == confirmPassword) {
                signUp(email, password)
            } else {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            }
        }
// Disables text selection and long click on the confirm password EditText view
        confirmPasswordEditText.apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
            isLongClickable = false
            setTextIsSelectable(false)
            setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
        }
    }

    private fun signUp(email: String, password: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.error_email_empty, Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, R.string.error_password_empty, Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.signInMethods?.let { signInMethods ->
                            if (signInMethods.isNotEmpty()) {
                                Toast.makeText(this, R.string.error_email_registered, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                                return@addOnCompleteListener
                            }
                    }
                } else {
                    Toast.makeText(this, R.string.error_fetch_signin_methods, Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { createUserTask ->
                        if (createUserTask.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, R.string.error_signup_failed, Toast.LENGTH_SHORT).show()
                        }
                    }

            }
    }




    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(authStateListener)
    }
}


