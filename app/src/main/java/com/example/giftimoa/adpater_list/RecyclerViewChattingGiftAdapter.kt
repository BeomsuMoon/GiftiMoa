package com.example.giftimoa.adpater_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.R
import com.example.giftimoa.dto.ChatItem

class RecyclerViewChattingGiftAdapter(private val chatList: List<ChatItem>) :
    RecyclerView.Adapter<RecyclerViewChattingGiftAdapter.ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chatting_cardview, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.username.text = chatItem.username
        holder.userMessage.text = chatItem.message
        holder.timestamp.text = chatItem.timestamp
        // Glide.with(holder.view.context).load(chatItem.profileImageUrl).into(holder.userProfile)
    }

    override fun getItemCount(): Int = chatList.size

    class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val userProfile: ImageView = view.findViewById(R.id.user_profile)
        val username: TextView = view.findViewById(R.id.user_nickname)
        val userMessage: TextView = view.findViewById(R.id.message)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }
}
