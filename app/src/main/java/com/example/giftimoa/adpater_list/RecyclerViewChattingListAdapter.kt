package com.example.giftimoa.adpater_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.databinding.ItemChattingCardviewBinding
import com.example.giftimoa.dto.ChatItem

class RecyclerViewChattingListAdapter : RecyclerView.Adapter<RecyclerViewChattingListAdapter.ViewHolder>() {
    private val chatRooms = mutableListOf<ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChattingCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        holder.bind(chatRoom)
    }

    override fun getItemCount() = chatRooms.size

    fun submitList(newChatRooms: List<ChatItem>) {
        chatRooms.clear()
        chatRooms.addAll(newChatRooms)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemChattingCardviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ChatItem: ChatItem) {
            //binding.userNickname.text = ChatItem.nickname
            binding.userMessage.text = ChatItem.message
        }
    }
}
