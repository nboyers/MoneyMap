package com.nobos.moneymap
import android.content.Intent
import android.os.Bundle
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
    }
}
