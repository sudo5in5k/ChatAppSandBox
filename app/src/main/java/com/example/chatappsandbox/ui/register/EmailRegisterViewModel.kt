package com.example.chatappsandbox.ui.register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class EmailRegisterViewModel : ViewModel() {

    val auth = FirebaseAuth.getInstance()
    var authStateListener: FirebaseAuth.AuthStateListener? = null

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _buttonEnabledState = MediatorLiveData<Boolean>()
    val buttonEnabledState: LiveData<Boolean>
        get() = _buttonEnabledState

    init {
        _buttonEnabledState.addSource(email) { validateLogin() }
        _buttonEnabledState.addSource(password) { validateLogin() }
    }

    private fun validateLogin() {
        _buttonEnabledState.value =
            (email.value?.isNotEmpty() ?: false) && (password.value?.isNotEmpty() ?: false)
    }

    fun registerButtonClicked(context: Context) {
        val email = email.value ?: return
        val password = password.value ?: return
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("ushi", user?.email)
                } else {
                    Toast.makeText(context, "Register failed: ${it.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        _buttonEnabledState.removeSource(email)
        _buttonEnabledState.removeSource(password)
    }
}