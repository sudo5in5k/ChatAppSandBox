package com.example.chatappsandbox.ui.userlist

import android.os.Bundle
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
import com.example.chatappsandbox.databinding.NavHeaderMainBinding
import com.example.chatappsandbox.util.Consts
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChatUserListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private var uid: String? = null
    private lateinit var adapter: ChatUserListAdapter

    private val viewModel: ChatUserListViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(
            application
        )
    }

    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var navHeaderMainBinding: NavHeaderMainBinding

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ChatUserListAdapter(viewModel)

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.lifecycleOwner = this

        activityMainBinding.appBar.content.also {
            it.viewModel = viewModel
            it.chatListRecycler.also {recyclerView ->
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.setHasFixedSize(true)
            }
        }

        setSupportActionBar(activityMainBinding.appBar.toolbar)
        activityMainBinding.navView.setNavigationItemSelectedListener(this)

        setupDrawer()
        setupHeaderBinding()

        uid = intent.getStringExtra(Consts.INTENT_UID)

        viewModel.loadUid(uid)
        viewModel.registerUserToDB(uid)
        viewModel.loadHeaderUserNameFromIntent(intent)
        viewModel.loadChatListItems(uid)

        viewModel.allUsers.observe(this) { list ->
            adapter.setAllUsers(list)
            viewModel.endLoading()
        }

        viewModel.logout.observe(this) { logout ->
            if (logout) {
                finish()
            }
        }
    }

    private fun setupDrawer() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            activityMainBinding.drawerLayout,
            activityMainBinding.appBar.toolbar,
            0,
            0
        )
        activityMainBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()
    }

    /**
     * xmlのapp:headerLayoutではbindされないため
     */
    private fun setupHeaderBinding() {
        navHeaderMainBinding =
            NavHeaderMainBinding.inflate(layoutInflater, activityMainBinding.navView, false)
        navHeaderMainBinding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
        activityMainBinding.navView.addHeaderView(navHeaderMainBinding.root)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_home -> {
                activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START)
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
        if (activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
