package com.example.giftimoa.ViewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.giftimoa.dto.ChatRoom
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.repository.ChatRepository
import com.example.giftimoa.repository.GiftAddRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(application: Application) : AndroidViewModel(application){
    private val context: Context = application
    private val _chatroomList = MutableLiveData<List<ChatRoom>>(emptyList())
    val chatroomList: LiveData<List<ChatRoom>> get() = _chatroomList


    // GiftAddRepository를 통해 데이터를 가져오는 함수 추가
    fun fetchHomeGiftsFromRepository(context: Context, userEmail: String) {
        viewModelScope.launch {
            try {
                // 백그라운드 스레드에서 GiftAddRepository를 통해 데이터를 가져오기
                val homeGifts = withContext(Dispatchers.IO) {
                    GiftAddRepository(context).fetchHomeGiftsFromServer(userEmail)
                }

                // LiveData에 업데이트
                _homeGifts.postValue(homeGifts)
            } catch (e: Exception) {
                Log.e("Gificon_ViewModel", "Error fetching home gifts: ${e.message}", e)
                // 오류 처리
            }
        }
    }
}
