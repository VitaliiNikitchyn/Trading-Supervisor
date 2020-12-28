package com.tradingsupervisor.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.tradingsupervisor.R
import com.tradingsupervisor.ui.fragment.AuthenticationFragment
import com.tradingsupervisor.ui.fragment.AuthenticationFragment.Companion.newInstance

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Handler().postDelayed({
            val sharedPref = getSharedPreferences(getString(R.string.appSharedPreferences), MODE_PRIVATE)
            val token = sharedPref.getString(getString(R.string.authToken), null)
            if (token == null) {
                val frameLayout = findViewById<FrameLayout>(R.id.start_activity_container)
                frameLayout.removeAllViews() //remove logo
                supportFragmentManager.beginTransaction()
                        .add(R.id.start_activity_container,
                                newInstance(), AuthenticationFragment.TAG)
                        .commit()
            } else {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 800)
    }
}