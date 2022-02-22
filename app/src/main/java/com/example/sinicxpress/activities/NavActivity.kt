package com.example.sinicxpress.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.sinicxpress.R
//import com.example.sinicxpress.activities.databinding.ActivityNavBinding
import com.example.sinicxpress.databinding.ActivityNavBinding
import com.google.firebase.auth.FirebaseAuth

class NavActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavBinding
    private lateinit var navView:NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNav.toolbar)

//        binding.appBarNav.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_nav)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        init()
    }

    private fun init() {
        navView.setNavigationItemSelectedListener { it->
            if (it.itemId == R.id.nav_sign_out)
            {
                val builder = AlertDialog.Builder(this@NavActivity)
                builder.setTitle("Sign Out")
                    .setMessage("Do you really want to sign Out?")
                    .setNegativeButton("Cancel",{ dialogInterface, _ -> dialogInterface.dismiss() })
                    .setPositiveButton("SIGN OUT"){ dialogInterface, _ ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@NavActivity,LoginActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }.setCancelable(false)

                val dialog = builder.create()
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(resources.getColor(R.color.black))
                }

                dialog.show()
            }
            true

        }

        val headerView = navView.getHeaderView(0)
        val txt_name = headerView.findViewById<View>(R.id.txt_name) as TextView
        val txt_phone = headerView.findViewById<View>(R.id.txt_phone) as TextView
        val txt_star = headerView.findViewById<View>(R.id.txt_star) as TextView

//        txt_name.setText(Common.buildWelcomeMessage())
//        txt_phone.setText(Common.currentUser!!.phoneNumber)
//        txt_star.setText(StringBuilder().append(Common.currentUser!!.rating))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_nav)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}