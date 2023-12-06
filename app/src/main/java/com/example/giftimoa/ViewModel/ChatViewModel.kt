package com.example.giftimoa.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.giftimoa.dto.ChatItem
import com.example.giftimoa.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChatRepository = ChatRepository()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val chatMessages: LiveData<List<ChatItem>> get() = repository.chatMessages

    fun fetchChatMessages(nickname: String) {
        viewModelScope.launch {
            try {
                repository.fetchChatMessages(nickname)
            } catch (e: Exception) {
                // Handle exceptions here
                Log.e("ChatViewModel", "Error fetching chat messages: ${e.message}")
                _errorMessage.postValue("Error fetching chat messages: ${e.message}")
            }
        }
    }

    fun sendMessage(nickname: String, receiverNickname: String, brand: String, message: String, timestamp: String) {
        viewModelScope.launch {
            try {
                repository.sendMessage(nickname, receiverNickname, brand, message, timestamp)
            } catch (e: Exception) {
                // Handle exceptions here
                Log.e("ChatViewModel", "Error sending message: ${e.message}")
                _errorMessage.postValue("Error sending message: ${e.message}")
            }
        }
    }
}