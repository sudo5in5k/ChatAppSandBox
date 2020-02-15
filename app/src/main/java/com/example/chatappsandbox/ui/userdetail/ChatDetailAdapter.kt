package com.example.chatappsandbox.ui.userdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappsandbox.databinding.ChatDetailItemBinding
import com.example.chatappsandbox.entity.Message

class ChatDetailAdapter(private val viewModel: ChatDetailViewModel) :
    RecyclerView.Adapter<ChatDetailAdapter.ViewHolder>() {

    private var archives: List<Message> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return archives.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val archive = archives[position]
        holder.binding.message = archive
        holder.binding.viewModel = viewModel
    }


    inner class ViewHolder(val binding: ChatDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setAllArchives(items: List<Message>) {
        archives = items
        notifyDataSetChanged()
    }
}