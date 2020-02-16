package com.example.chatappsandbox.ui.userdetail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappsandbox.R
import com.example.chatappsandbox.databinding.ActivityDetailChatBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChatDetailActivity : AppCompatActivity() {

    @ExperimentalCoroutinesApi
    private val viewModel: ChatDetailViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(
            application
        )
    }

    lateinit var activityDetailChatBinding: ActivityDetailChatBinding
    lateinit var adapter: ChatDetailAdapter

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * From intent
         */
        viewModel.preserveExtras(intent)

        activityDetailChatBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail_chat)
        activityDetailChatBinding.also {
            it.activity = this
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

        viewModel.loadArchiveForMe()
        viewModel.loadArchiveFromMe()
    }
}