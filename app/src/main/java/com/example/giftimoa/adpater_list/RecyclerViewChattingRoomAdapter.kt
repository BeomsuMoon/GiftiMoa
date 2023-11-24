package com.example.giftimoa.adpater_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.databinding.ItemChattingDateBinding
import com.example.giftimoa.databinding.ItemChattingMessageBinding
import com.example.giftimoa.dto.ChatItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerViewChattingRoomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

    inner class DateViewHolder(private val binding: ItemChattingDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatItem) {
            binding.chattingDate.text = message.message
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_DATE) {
            val dateBinding = ItemChattingDateBinding.inflate(inflater, parent, false)
            DateViewHolder(dateBinding)
        } else {
            val messageBinding = ItemChattingMessageBinding.inflate(inflater, parent, false)
            ChatMessageViewHolder(messageBinding)
        }
    }

    // ViewHolder와 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is ChatMessageViewHolder) {
            holder.bind(message)
        } else if (holder is DateViewHolder) {
            holder.bind(message)
        }
    }

    // 아이템 타입 반환
    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isDate) TYPE_DATE else TYPE_MESSAGE
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

    companion object {
        private const val TYPE_MESSAGE = 0
        private const val TYPE_DATE = 1
    }
}

