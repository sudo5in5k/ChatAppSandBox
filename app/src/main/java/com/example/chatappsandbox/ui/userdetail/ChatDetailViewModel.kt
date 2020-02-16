package com.example.chatappsandbox.ui.userdetail

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappsandbox.entity.Message
import com.example.chatappsandbox.util.Consts
import com.example.chatappsandbox.util.fetchMessageArchiveWithFlow
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
class ChatDetailViewModel(val app: Application) : AndroidViewModel(app) {

    private val db = FirebaseDatabase.getInstance()
    val isLoading = MutableLiveData<Boolean>(false)

    val allArchives = MediatorLiveData<List<Message>>()
    private val archivesForMe = MutableLiveData<List<Message>>()
    private val archivesFromMe = MutableLiveData<List<Message>>()

    val draft = MutableLiveData<String?>()
    val sendClickable = MediatorLiveData<Boolean>()

    private val message = MutableLiveData<Message?>()
    private val uid = MutableLiveData<String?>()

    init {
        sendClickable.addSource(draft) { sendClickable.value = it?.isNotEmpty() ?: false }

        allArchives.addSource(archivesForMe) { combineLatest() }
        allArchives.addSource(archivesFromMe) { combineLatest() }
    }

    fun preserveExtras(intent: Intent) {
        message.value =
            intent.getSerializableExtra(Consts.INTENT_TO_CHAT_DETAIL_ACTIVITY) as? Message
        uid.value = intent.getStringExtra(Consts.INTENT_UID)
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

    fun endLoading() {
        isLoading.postValue(false)
    }

    fun loadArchiveForMe() {
        val uid = uid.value ?: return
        val message = message.value ?: return
        val readDataFlow = db.getReference("messages/$uid").fetchMessageArchiveWithFlow()
        viewModelScope.launch {
            readDataFlow.collect {
                archivesForMe.postValue(mapMessageToArchive(message, it))
            }
        }
    }

    fun loadArchiveFromMe() {
        val uid = uid.value ?: return
        val message = message.value ?: return
        val readDataFlow = db.getReference("messages/${message.from}").fetchMessageArchiveWithFlow()
        viewModelScope.launch {
            readDataFlow.collect {
                archivesFromMe.postValue(mapMessageToArchiveFromMe(uid, it))
            }
        }
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

    fun onSendClick(activity: ChatDetailActivity) {
        val uid = uid.value ?: return
        val message = message.value ?: return
        val formatter = SimpleDateFormat("yyyyMMddHHmm", Locale.JAPAN)
        try {
            val time = formatter.format(Calendar.getInstance().time)
            val newMessage = Message(uid, draft.value!!, time.toLong())
            val key = db.getReference("messages/${message.from}").push().key ?: return

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    db.getReference("messages/${message.from}").child(key).setValue(newMessage)
                }
                clearDraft()
            }
        } catch (e: Exception) {
            Log.d("debug", "${e.message}")
            return
        } finally {
            closeKeyBoard(activity)
        }
    }

    private fun closeKeyBoard(activity: ChatDetailActivity) {
        val inputManager =
            app.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                ?: return
        inputManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun clearDraft() {
        draft.value = null
    }

    override fun onCleared() {
        super.onCleared()
        sendClickable.removeSource(draft)

        allArchives.removeSource(archivesForMe)
        allArchives.removeSource(archivesFromMe)
    }
}