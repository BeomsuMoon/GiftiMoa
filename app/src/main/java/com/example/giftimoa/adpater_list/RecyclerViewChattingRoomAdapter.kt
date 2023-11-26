package com.example.giftimoa.adpater_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.databinding.ItemChattingDateBinding
import com.example.giftimoa.databinding.ItemChattingMessageBinding
import com.example.giftimoa.databinding.ItemChattingMessageOtherBinding
import com.example.giftimoa.dto.ChatItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerViewChattingRoomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SELF = 1
    private val ITEM_OTHER = 2

    private val diffcallback = object :DiffUtil.ItemCallback<ChatItem>(){
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffcallback)

    fun submitData(chats: List<ChatItem>) {
        differ.submitList(chats)
    }


    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return if(viewType == ITEM_SELF) {
           val binding = ItemChattingMessageBinding.inflate(LayoutInflater.from(parent.context), parent,false)
            SelfChatItemViewHolder(binding)
       }else{
           val binding = ItemChattingMessageOtherBinding.inflate(LayoutInflater.from(parent.context), parent,false)
           OtherChatItemViewHolder(binding)
           }
       }

    // ViewHolder와 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val chat = differ.currentList[position]
        if(chat.isSelf){
            (holder as SelfChatItemViewHolder).bind(chat)
        }else{
            (holder as OtherChatItemViewHolder).bind(chat)
        }
    }
    inner class DateViewHolder(private val binding: ItemChattingDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatItem) {
            binding.chattingDate.text = message.message
        }
    }
    inner class OtherChatItemViewHolder(val binding: ItemChattingMessageOtherBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chat:ChatItem){
            binding.apply {
                txtNickname.text = chat.nickname
                txtMessage.text = chat.message
                txtDate.text = formatTimestamp(chat.timestamp)
            }

        }
        private fun formatTimestamp(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }
    }
    inner class SelfChatItemViewHolder(val binding: ItemChattingMessageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chat:ChatItem){
            binding.apply {
                txtMessage.text = chat.message
                txtDate.text = formatTimestamp(chat.timestamp)
            }
        }
        private fun formatTimestamp(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }
    }
     override fun getItemViewType(position: Int): Int {
        val chat = differ.currentList[position]
        return if (chat.isSelf) ITEM_SELF else ITEM_OTHER
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

