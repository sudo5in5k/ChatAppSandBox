package com.example.chatappsandbox.ui.userdetail

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappsandbox.entity.Message
import com.example.chatappsandbox.util.fetchMessageArchiveWithFlow
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatDetailViewModel(private val app: Application) : AndroidViewModel(app) {

    private val db = FirebaseDatabase.getInstance()
    val isLoading = MutableLiveData<Boolean>(false)
    val sendClickable = MutableLiveData<Boolean>(false)
    val allArchives = MutableLiveData<List<Message>>()

    private fun performLoading() {
        isLoading.postValue(true)
    }

    @ExperimentalCoroutinesApi
    fun loadArchives(uid: String?, message: Message?) {
        uid ?: return
        message ?: return
        performLoading()
        val readDataFlow = db.getReference("messages/$uid").fetchMessageArchiveWithFlow()
        viewModelScope.launch {
            readDataFlow.collect {
                allArchives.postValue(mapMessageToArchive(message, it))
            }
        }
    }

    fun endLoading() {
        isLoading.postValue(false)
    }

    /**
     * ユーザーからこれまでのアーカイブを特定して返す
     * TODO to repository
     */
    private fun mapMessageToArchive(message: Message, list: List<Message>) =
        list.filter { it.from == message.from }.sortedBy { it.time }

    fun onSendClick() {

    }
}