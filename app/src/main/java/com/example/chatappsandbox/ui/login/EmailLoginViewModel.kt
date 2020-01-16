package com.example.chatappsandbox.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EmailLoginViewModel : ViewModel() {

    private val _registerClicked = MutableLiveData<Boolean>(false)
    val registerClicked: LiveData<Boolean>
        get() = _registerClicked

    fun onRegisterTextClicked() {
        _registerClicked.postValue(true)
    }
}