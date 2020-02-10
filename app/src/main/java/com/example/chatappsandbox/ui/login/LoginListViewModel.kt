package com.example.chatappsandbox.ui.login

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginListViewModel(val app: Application) : AndroidViewModel(app) {

    private val _signIn = MutableLiveData<Boolean>(false)
    val signIn: LiveData<Boolean>
        get() = _signIn

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val workManager: WorkManager by lazy { WorkManager.getInstance(app.applicationContext) }
    private val request: OneTimeWorkRequest.Builder by lazy { OneTimeWorkRequest.Builder(LoginWorker::class.java) }
    private val constraints = Constraints.Builder().setRequiresDeviceIdle(true).build()

    fun onEmailLoginClicked() {
        _signIn.postValue(true)
    }

    fun onGoogleLoginClicked() {
        //TODO
    }

    fun onTwitterLoginClicked() {
        //TODO
    }

    fun onFacebookLoginClicked() {
        val data = Data.Builder().apply { putString("FACEBOOK", "FaceBook") }.build()
        request.setInputData(data)
        workManager.enqueue(request.build())
    }
}