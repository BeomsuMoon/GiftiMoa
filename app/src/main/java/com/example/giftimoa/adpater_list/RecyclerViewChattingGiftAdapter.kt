package com.example.giftimoa.adpater_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.R
import com.example.giftimoa.databinding.ItemChattingMessageBinding
import com.example.giftimoa.dto.ChatItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerViewChattingGiftAdapter : RecyclerView.Adapter<RecyclerViewChattingGiftAdapter.ChatMessageViewHolder>() {
    private val messageList: MutableList<ChatItem> = mutableListOf()

    // ViewHolder 클래스
    inner class ChatMessageViewHolder(private val binding: ItemChattingMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatItem) {
            binding.txtMessage.text = message.message
            binding.txtDate.text = formatTimestamp(message.timestamp)
        }

        private fun formatTimestamp(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChattingMessageBinding.inflate(inflater, parent, false)
        return ChatMessageViewHolder(binding)
    }

    // ViewHolder와 데이터 바인딩
    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int {
        return messageList.size
    }

    // 메시지 추가
    fun addMessage(message: ChatItem) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }
}
