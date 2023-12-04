package com.example.giftimoa.adpater_list

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.Chatting_room_activity
import com.example.giftimoa.dto.ChatRoom
import com.example.giftimoa.databinding.ItemChattingCardviewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class RecyclerViewChattingRoomAdapter : RecyclerView.Adapter<RecyclerViewChattingRoomAdapter.ChatRoomViewHolder>() {
    private val chatRooms = mutableListOf<ChatRoom>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemChattingCardviewBinding.inflate(layoutInflater, parent, false)
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        holder.bind(chatRoom)
    }

    override fun getItemCount(): Int = chatRooms.size

    inner class ChatRoomViewHolder(private val binding: ItemChattingCardviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRoom: ChatRoom) {
            Log.d("로그", "Bind ViewHolder with: $chatRoom")
            binding.userNickname.text = chatRoom.nickname
            binding.userMessage.text = chatRoom.last_message
            binding.timestamp.text = formatTimestamp(chatRoom.last_date)

            // 채팅방 아이템 클릭 이벤트 처리
            itemView.setOnClickListener {
                val intent = Intent(it.context, Chatting_room_activity::class.java)
                intent.putExtra("chatroom_id", chatRoom.id)
                it.context.startActivity(intent)
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("MM월 dd일", Locale.getDefault())
            return format.format(date)
        }
    }

    fun setChatRooms(chatRooms: List<ChatRoom>) {
        this.chatRooms.clear()
        this.chatRooms.addAll(chatRooms)
        notifyDataSetChanged()
    }

    fun submitList(chatRooms: List<ChatRoom>) {
        setChatRooms(chatRooms)
    }
}