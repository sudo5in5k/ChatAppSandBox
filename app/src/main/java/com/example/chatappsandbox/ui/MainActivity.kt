package com.example.chatappsandbox.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.chatappsandbox.R
import com.example.chatappsandbox.entity.UserInfo
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val db = FirebaseDatabase.getInstance()
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        nav_view.setNavigationItemSelectedListener(this)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, 0, 0)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()

        uid = intent.getStringExtra("uid")
        val username = intent.getStringExtra("user_name")
        val email = intent.getStringExtra("email")
        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show()
        // register
        if (uid != null) {
            db.getReference("users/${uid}").setValue(UserInfo(username, email))
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_home -> {
                db.getReference("users/${uid}")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            return
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            p0.children.forEach {
                                Log.d("debug", "key: ${it.key}, value: ${it.value}")
                            }
                        }
                    })
                drawer_layout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_logout -> {
                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return true
            }
        }
        return true
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
