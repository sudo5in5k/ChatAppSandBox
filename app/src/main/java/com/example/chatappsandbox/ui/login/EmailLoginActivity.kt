package com.example.chatappsandbox.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.chatappsandbox.R
import com.example.chatappsandbox.databinding.ActivityEmailLoginBinding
import com.example.chatappsandbox.ui.register.EmailRegisterActivity

class EmailLoginActivity: AppCompatActivity() {

    private val viewModel: EmailLoginViewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(EmailLoginViewModel::class.java)
    }

    lateinit var activityEmailLoginBinding: ActivityEmailLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityEmailLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_email_login)
        activityEmailLoginBinding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        viewModel.registerClicked.observe(this) {
            if (it) {
                //TODO forResult
                startActivity(Intent(this, EmailRegisterActivity::class.java))
            }
        }
    }
}