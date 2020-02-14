package com.example.chatappsandbox.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappsandbox.entity.Message
import com.example.chatappsandbox.entity.UserInfo
import com.example.chatappsandbox.util.Consts
import com.example.chatappsandbox.util.fetchMessageArchive
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ChatUserListViewModel(val app: Application) : AndroidViewModel(app) {

    private val db = FirebaseDatabase.getInstance()

    val allUsers = MutableLiveData<List<Message>>()
    val logout = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)

    val headerUserName = MutableLiveData<String>()
    val headerMailAddress = MutableLiveData<String>()

    fun performLogout() {
        AuthUI.getInstance().signOut(app.applicationContext).addOnCompleteListener {
            logout.postValue(true)
        }
    }

    private fun performLoading() {
        isLoading.postValue(true)
    }

    fun endLoading() {
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

    fun loadHeaderUserNameFromIntent(intent: Intent) {
        headerUserName.postValue(intent.getStringExtra(Consts.INTENT_USER_NAME))
        headerMailAddress.postValue(intent.getStringExtra(Consts.INTENT_MAIL_ADDRESS))
    }

    fun registerUserToDB(uid: String?) {
        if (uid != null) {
            db.getReference("users/${uid}").setValue(UserInfo(headerUserName.value, headerMailAddress.value))
        }
    }
}