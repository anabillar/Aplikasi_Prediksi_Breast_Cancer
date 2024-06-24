package com.example.breast_cancer

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_profile)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
            R.id.nav_data -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DataFragment())
                .commit()
            R.id.nav_feature -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FeatureFragment())
                .commit()
            R.id.nav_modeling -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ModelFragment())
                .commit()
            R.id.nav_simulasimodel -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SimulasiFragment())
                .commit()
            else -> return false
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
