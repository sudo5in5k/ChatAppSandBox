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

@ExperimentalCoroutinesApi
class ChatDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val db = FirebaseDatabase.getInstance()
    val isLoading = MutableLiveData<Boolean>(false)

    val allArchives = MediatorLiveData<List<Message>>()
    private val archivesForMe = MutableLiveData<List<Message>>()
    private val archivesFromMe = MutableLiveData<List<Message>>()

    val draft = MutableLiveData<String?>()
    val sendClickable = MediatorLiveData<Boolean>()

    init {
        sendClickable.addSource(draft) { sendClickable.value = it?.isNotEmpty() ?: false }

        allArchives.addSource(archivesForMe) { combineLatest() }
        allArchives.addSource(archivesFromMe) { combineLatest() }
    }

    private fun combineLatest() {
        performLoading()
        allArchives.value =
            (archivesFromMe.value ?: arrayListOf()).plus(archivesForMe.value ?: arrayListOf())
                .sortedBy { it.time }
    }

    private fun performLoading() {
        isLoading.postValue(true)
    }

    fun loadArchiveForMe(uid: String?, message: Message?) {
        uid ?: return
        message ?: return
        val readDataFlow = db.getReference("messages/$uid").fetchMessageArchiveWithFlow()
        viewModelScope.launch {
            readDataFlow.collect {
                archivesForMe.postValue(mapMessageToArchive(message, it))
            }
        }
    }

    fun loadArchiveFromMe(uid: String?, message: Message?) {
        uid ?: return
        val from = message?.from ?: return
        val readDataFlow = db.getReference("messages/$from").fetchMessageArchiveWithFlow()
        viewModelScope.launch {
            readDataFlow.collect {
                archivesFromMe.postValue(mapMessageToArchiveFromMe(uid, it))
            }
        }
    }

    fun endLoading() {
        isLoading.postValue(false)
    }


    /**
     * 自分が受信したこれまでのアーカイブを返す
     * TODO to repository
     */
    private fun mapMessageToArchive(message: Message, list: List<Message>) =
        list.filter { it.from == message.from }

    /**
     * 自分が送信したこれまでのアーカイブを返す
     */
    private fun mapMessageToArchiveFromMe(uid: String, list: List<Message>) =
        list.filter { it.from == uid }

    fun onSendClick() {
        Log.d("debug", "send click")
    }

    override fun onCleared() {
        super.onCleared()
        sendClickable.removeSource(draft)

        allArchives.removeSource(archivesForMe)
        allArchives.removeSource(archivesFromMe)
    }
}