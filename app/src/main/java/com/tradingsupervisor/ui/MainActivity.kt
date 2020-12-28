package com.tradingsupervisor.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.tradingsupervisor.R
import com.tradingsupervisor.ui.fragment.AccountFragment
import com.tradingsupervisor.ui.fragment.MapsFragment
import com.tradingsupervisor.ui.fragment.MyShopsFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction()
                .replace(R.id.navigation_drawer_content, MapsFragment.newInstance(), MapsFragment.TAG)
                .commit()
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment?
        val transaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.nav_my_location -> {
                fragment = supportFragmentManager.findFragmentByTag(MapsFragment.TAG)
                if (fragment == null) fragment = MapsFragment.newInstance()
                transaction.replace(R.id.navigation_drawer_content, fragment, MapsFragment.TAG)
                        //.addToBackStack(null) //we can open it from navDrawer
                        .commit()
            }
            R.id.nav_reports -> {
                fragment = supportFragmentManager.findFragmentByTag(MyShopsFragment.TAG)
                if (fragment == null) fragment = MyShopsFragment.newInstance()
                transaction.replace(R.id.navigation_drawer_content, fragment, MyShopsFragment.TAG)
                        .commit()
            }
            R.id.nav_account -> {
                fragment = supportFragmentManager.findFragmentByTag(AccountFragment.TAG)
                if (fragment == null) fragment = AccountFragment.newInstance()
                transaction.replace(R.id.navigation_drawer_content, fragment!!, AccountFragment.TAG)
                        .commit()
            }
            R.id.nav_logout -> {
                val sharedPref = getSharedPreferences(getString(R.string.appSharedPreferences), MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.remove(getString(R.string.authToken))
                editor.apply()
                finish()
            }
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}