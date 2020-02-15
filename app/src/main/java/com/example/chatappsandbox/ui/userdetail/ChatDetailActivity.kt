package com.example.chatappsandbox.ui.userdetail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappsandbox.R
import com.example.chatappsandbox.databinding.ActivityDetailChatBinding
import com.example.chatappsandbox.entity.Message
import com.example.chatappsandbox.util.Consts
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChatDetailActivity : AppCompatActivity() {

    private val viewModel: ChatDetailViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(
            application
        )
    }

    private var message: Message? = null
    private var uid: String? = null

    lateinit var activityDetailChatBinding: ActivityDetailChatBinding
    lateinit var adapter: ChatDetailAdapter

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDetailChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_chat)
        activityDetailChatBinding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        adapter = ChatDetailAdapter(viewModel)

        activityDetailChatBinding.charDetailRecycler.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this)
            it.setHasFixedSize(true)
        }

        viewModel.allArchives.observe(this) {
            adapter.setAllArchives(it)
            viewModel.endLoading()
        }

        /**
         * From intent
         */
        message = intent.getSerializableExtra(Consts.INTENT_TO_CHAT_DETAIL_ACTIVITY) as? Message
        uid = intent.getStringExtra(Consts.INTENT_UID)

        viewModel.loadArchives(uid, message)
    }
}