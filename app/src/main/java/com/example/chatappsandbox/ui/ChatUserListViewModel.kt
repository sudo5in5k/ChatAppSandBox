package com.example.chatappsandbox.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappsandbox.entity.Message
import com.example.chatappsandbox.util.fetchMessageArchive
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ChatUserListViewModel(val app: Application) : AndroidViewModel(app) {

    val allUsers = MutableLiveData<List<Message>>()
    val logout = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)
    private val db = FirebaseDatabase.getInstance()

    fun performLogout() {
        AuthUI.getInstance().signOut(app.applicationContext).addOnCompleteListener {
            logout.postValue(true)
        }
    }

    private fun performLoading() {
        isLoading.postValue(true)
    }

    fun endLoading() {
        Log.d("debug", "end loading?")
        isLoading.postValue(false)
    }

    fun loadChatListItems(uid: String?) {
        uid ?: return
        performLoading()
        viewModelScope.launch {
            val list = db.getReference("messages/$uid").fetchMessageArchive()
            allUsers.postValue(list)
        }
    }
}