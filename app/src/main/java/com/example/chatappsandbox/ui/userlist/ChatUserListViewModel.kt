package com.example.chatappsandbox.ui.userlist

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappsandbox.entity.Message
import com.example.chatappsandbox.entity.UserInfo
import com.example.chatappsandbox.ui.userdetail.ChatDetailActivity
import com.example.chatappsandbox.util.Consts
import com.example.chatappsandbox.util.fetchMessageArchiveWithFlow
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatUserListViewModel(val app: Application) : AndroidViewModel(app) {

    private val db = FirebaseDatabase.getInstance()

    val allUsers = MutableLiveData<List<Message>>()
    val logout = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)
    val uid = MutableLiveData<String?>()

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

    @ExperimentalCoroutinesApi
    fun loadChatListItems(uid: String?) {
        uid ?: return
        performLoading()
        val readDataFlow = db.getReference("messages/$uid").fetchMessageArchiveWithFlow()
        viewModelScope.launch {
            readDataFlow.collect {
                allUsers.postValue(mapToUserList(it))
            }
        }
    }

    fun loadUid(id: String?) {
        uid.postValue(id)
    }

    /**
     * メッセージ履歴からリストに表示するユーザを新しい順かつ一意に表示する
     */
    private fun mapToUserList(list: List<Message>) =
        list.sortedByDescending { it.time }.distinctBy { it.from }

    fun loadHeaderUserNameFromIntent(intent: Intent) {
        headerUserName.postValue(intent.getStringExtra(Consts.INTENT_USER_NAME))
        headerMailAddress.postValue(intent.getStringExtra(Consts.INTENT_MAIL_ADDRESS))
    }

    fun registerUserToDB(uid: String?) {
        if (uid != null) {
            db.getReference("users/${uid}")
                .setValue(UserInfo(headerUserName.value, headerMailAddress.value))
        }
    }

    fun onItemClick(uid: String?, message: Message) {
        val intent = Intent(getApplication(), ChatDetailActivity::class.java).also {
            it.putExtra(Consts.INTENT_TO_CHAT_DETAIL_ACTIVITY, message)
            it.putExtra(Consts.INTENT_UID, uid)
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        app.startActivity(intent)
    }
}