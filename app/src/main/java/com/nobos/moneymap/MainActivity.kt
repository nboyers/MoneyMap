package com.nobos.moneymap



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nobos.moneymap.firebase.SignUpActivity
import com.nobos.moneymap.fragments.SummaryFragment
import com.nobos.moneymap.interfaces.OnMonthSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnMonthSelectedListener {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            // Check if the user is logged in
            val currentUser = mAuth.currentUser
            if (currentUser == null) {
                // Navigate to SignUpActivity if the user is not signed in
                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
                    finish()
                }
            }
        }

        // Add the SummaryFragment to the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.summaryFragmentContainer, SummaryFragment())
            .commit()
    }

    override fun onMonthSelected(month: Int, year: Int) {
        val fragmentManager = supportFragmentManager
        val summaryFragment = fragmentManager.findFragmentById(R.id.summaryFragmentContainer)
        if (summaryFragment is SummaryFragment) {
            summaryFragment.showChartsForMonth(month, year)
        }
    }
}


