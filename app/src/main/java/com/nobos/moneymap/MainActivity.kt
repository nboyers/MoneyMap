package com.nobos.moneymap


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.nobos.moneymap.firebase.SignUpActivity
import com.nobos.moneymap.fragments.MyPagerAdapter
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

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

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = MyPagerAdapter(this)

        // Connect the TabLayout with the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Summary"
                1 -> tab.text = "Charts"
            }
        }.attach()
    }
}
