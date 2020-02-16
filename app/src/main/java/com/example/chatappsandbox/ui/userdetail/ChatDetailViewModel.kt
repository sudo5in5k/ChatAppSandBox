package com.example.chatappsandbox.ui.userdetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappsandbox.entity.Message
import com.example.chatappsandbox.util.fetchMessageArchiveWithFlow
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val db = FirebaseDatabase.getInstance()
    val isLoading = MutableLiveData<Boolean>(false)
    val allArchives = MutableLiveData<List<Message>>()

    val draft = MutableLiveData<String?>()
    val sendClickable = MediatorLiveData<Boolean>()

    init {
        sendClickable.addSource(draft) {
            sendClickable.value = it?.isNotEmpty() ?: false
        }
    }

    private fun performLoading() {
        isLoading.postValue(true)
    }

    @ExperimentalCoroutinesApi
    fun loadArchives(uid: String?, message: Message?) {
        uid ?: return
        message ?: return
        performLoading()
        //TODO ２つとってきたのち整列させる
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
        Log.d("debug", "send click")
    }

    override fun onCleared() {
        super.onCleared()
        sendClickable.removeSource(draft)
    }
}