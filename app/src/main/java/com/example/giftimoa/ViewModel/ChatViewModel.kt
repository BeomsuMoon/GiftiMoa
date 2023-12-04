package com.example.giftimoa.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.giftimoa.dto.ChatRoom
import com.example.giftimoa.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    private val _chatRooms = MutableLiveData<List<ChatRoom>?>()
    val chatRooms: LiveData<List<ChatRoom>?> get() = _chatRooms

    fun fetchChatRooms(nickname: String) {
        Log.d("ChatViewModel", "fetchChatRooms is called with nickname: $nickname")
        viewModelScope.launch {
            try {
                val chatRooms = repository.getChatRooms(nickname)
                _chatRooms.postValue(chatRooms)
            } catch (e: Exception) {
                Log.e("Chat_ViewModel", "Error fetching data: ${e.message}", e)
            }
        }
    }

}