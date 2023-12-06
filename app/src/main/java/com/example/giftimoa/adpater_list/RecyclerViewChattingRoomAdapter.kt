package com.example.giftimoa.adpater_list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.databinding.ItemChattingCardviewBinding
import com.example.giftimoa.dto.ChatItem
class RecyclerViewChattingRoomAdapter(private val onChatItemClick: (ChatItem) -> Unit) :
    RecyclerView.Adapter<RecyclerViewChattingRoomAdapter.ChatItemViewHolder>() {
    private val chatItem = mutableListOf<ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemChattingCardviewBinding.inflate(layoutInflater, parent, false)
        return ChatItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        val chatItem = chatItem[position]
        holder.bind(chatItem)
    }

    override fun getItemCount(): Int = chatItem.size

    inner class ChatItemViewHolder(private val binding: ItemChattingCardviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatItem: ChatItem) {
            Log.d("로그", "Bind ViewHolder with: $chatItem")
            binding.userNickname.text = chatItem.nickname
            binding.userMessage.text = chatItem.message
            binding.brand.text = chatItem.brand
            binding.timestamp.text = chatItem.timestamp

            // 채팅방 아이템 클릭 이벤트 처리
            binding.root.setOnClickListener {
                onChatItemClick(chatItem) // 클릭된 아이템 데이터를 ChatFragment로 전달
            }
        }
    }

    fun setChatItem(chatItem: List<ChatItem>) {
        this.chatItem.clear()
        this.chatItem.addAll(chatItem)
        notifyDataSetChanged()
    }

    fun submitList(chatItem: List<ChatItem>) {
        setChatItem(chatItem)
    }


}