package com.example.chatappsandbox.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappsandbox.R
import com.example.chatappsandbox.databinding.ActivityMainBinding
import com.example.chatappsandbox.entity.UserInfo
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val db = FirebaseDatabase.getInstance()
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var uid: String? = null
    private lateinit var adapter: ChatUserListAdapter

    private val viewModel: ChatUserListViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(
            application
        )
    }

    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ChatUserListAdapter(viewModel)

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.lifecycleOwner = this

        activityMainBinding.appBar.content.viewModel = viewModel
        activityMainBinding.appBar.content.chatListRecycler.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this)
            it.setHasFixedSize(true)
        }

        setSupportActionBar(toolbar)

        nav_view.setNavigationItemSelectedListener(this)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, 0, 0)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()

        uid = intent.getStringExtra("uid")
        val username = intent.getStringExtra("user_name")
        val email = intent.getStringExtra("email")

        // register
        if (uid != null) {
            db.getReference("users/${uid}").setValue(UserInfo(username, email))
        }

        viewModel.loadChatListItems(uid)

        viewModel.allUsers.observe(this) { list ->
            list.forEach {
                Log.d("debug", "from: ${it.from}")
                Log.d("debug", "text: ${it.text}")
            }
            viewModel.endLoading()
        }

        viewModel.logout.observe(this) { logout ->
            if (logout) {
                finish()
            }
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_home -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_logout -> {
                viewModel.performLogout()
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
