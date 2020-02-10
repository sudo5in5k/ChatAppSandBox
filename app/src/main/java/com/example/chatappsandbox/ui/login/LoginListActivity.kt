package com.example.chatappsandbox.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.chatappsandbox.R
import com.example.chatappsandbox.databinding.ActivityLoginListBinding
import com.example.chatappsandbox.ui.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class LoginListActivity : AppCompatActivity() {

    private val viewModel: LoginListViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application)
            .create(LoginListViewModel::class.java)
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
                val providers = listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher_round)
                        .setTheme(R.style.ThemeOverlay_AppCompat_Dark)
                        .setTosAndPrivacyPolicyUrls(
                            "https://policies.google.com/terms?hl=ja",
                            "https://policies.google.com/privacy?hl=ja"
                        )
                        .build(),
                    RC_SIGN_IN
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java).also {
                it.putExtra("uid", viewModel.auth.currentUser?.uid)
                it.putExtra("user_name", viewModel.auth.currentUser?.displayName)
                it.putExtra("email", viewModel.auth.currentUser?.email)
            })
            Toast.makeText(this, "already sign in", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "not sign in", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser?.displayName ?: "Null Name"
                val intent = Intent(this, MainActivity::class.java).also {
                    it.putExtra("user_name", user)
                }
                startActivity(intent)
            } else {
                if (response == null) {
                    Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_LONG).show()
                    return
                }
                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show()
                    return
                }

                Toast.makeText(this, "Sign in failed: result == ${response.error}", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 0x100
    }
}