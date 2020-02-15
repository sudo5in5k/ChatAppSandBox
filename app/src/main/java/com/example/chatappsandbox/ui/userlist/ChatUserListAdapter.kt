package com.example.chatappsandbox.ui.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappsandbox.databinding.ChatUserListItemBinding
import com.example.chatappsandbox.entity.Message

class ChatUserListAdapter(private val viewModel: ChatUserListViewModel) :
    RecyclerView.Adapter<ChatUserListAdapter.ViewHolder>() {

    /**
     * TODO メッセージ一覧から整形して使う
     */
    private var users: List<Message> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatUserListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return users.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users = users[position]
        holder.binding.message = users
        holder.binding.viewModel = viewModel
    }


    inner class ViewHolder(val binding: ChatUserListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setAllUsers(items: List<Message>) {
        users = items
        notifyDataSetChanged()
    }
}