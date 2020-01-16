package com.example.chatappsandbox.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.chatappsandbox.R
import com.example.chatappsandbox.databinding.ActivityLoginListBinding
import com.example.chatappsandbox.ui.register.EmailRegisterActivity

class LoginListActivity : AppCompatActivity() {

    private val viewModel: LoginListViewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(LoginListViewModel::class.java)
    }

    private lateinit var activityLoginListBinding: ActivityLoginListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login_list)
        activityLoginListBinding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        viewModel.signIn.observe(this) {
            if (it) {
                startActivity(Intent(this, EmailLoginActivity::class.java))
            }
        }
    }
}