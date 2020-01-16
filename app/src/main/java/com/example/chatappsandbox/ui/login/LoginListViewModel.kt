package com.example.chatappsandbox.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginListViewModel : ViewModel() {

    private val _signIn = MutableLiveData<Boolean>(false)
    val signIn: LiveData<Boolean>
        get() = _signIn

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
        //TODO
    }
}