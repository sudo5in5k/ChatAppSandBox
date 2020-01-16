package com.example.chatappsandbox.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.chatappsandbox.R
import com.example.chatappsandbox.databinding.ActivityEmailRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class EmailRegisterActivity : AppCompatActivity() {

    private val emailRegisterViewModel: EmailRegisterViewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(EmailRegisterViewModel::class.java)
    }

    lateinit var auth: FirebaseAuth

    private lateinit var activityLoginBinding: ActivityEmailRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_email_register)
        activityLoginBinding.also {
            it.viewModel = emailRegisterViewModel
            it.lifecycleOwner = this
        }

        initFirebase()

        emailRegisterViewModel.buttonEnabledState.observe(this) {
            activityLoginBinding.register.isEnabled = it
        }
        activityLoginBinding.register.setOnClickListener {
            emailRegisterViewModel.registerButtonClicked(this)
        }
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        emailRegisterViewModel.authStateListener = FirebaseAuth.AuthStateListener {
            it.currentUser ?: return@AuthStateListener
            //val uid = user.uid
        }
    }
}