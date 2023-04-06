package com.nobos.moneymap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.nobos.moneymap.firebase.SignUpActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        // Check if the user is logged in
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            // Navigate to SignUpActivity if the user is not signed in
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        } else {
            setContentView(R.layout.activity_main)

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
}
