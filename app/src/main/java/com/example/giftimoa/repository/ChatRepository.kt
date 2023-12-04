package com.example.giftimoa.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.ChatRoom
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import android.util.Log
import com.example.giftimoa.dto.Home_gift
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

/*class ChatRepository {
    // MutableLiveData를 사용하여 홈 기프트 목록을 보관
    private val _chatroomList: MutableLiveData<List<ChatRoom>> = MutableLiveData()
    val chatroomList: LiveData<List<ChatRoom>>
        get() = _chatroomList

    // 서버에서 채팅 목록을 가져오는 함수
    suspend fun fetchChatRoomFromServer(nickname: String): List<ChatRoom> {
        var response: Response? = null
        try {
            // API에 따라 URL 및 기타 매개변수 조정
            Log.d("ChatRepository", "fetcChatRoomFromServer - nickname: $nickname")
            val url = "http://3.35.110.246:3306/chatList"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val client = OkHttpClient.Builder()
                .build()

            response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // 서버 응답을 처리하고 MutableLiveData를 업데이트
                val jsonResponse = response.body?.string()
                Log.d("Chatrepository", "Server response: $jsonResponse")
                val chatroomList = parseHomeGifts(jsonResponse)
                _chatroomList.postValue(chatroomList)
                return chatroomList
            } else {
                // 서버 오류 처리
                val errorBody = response?.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("Failed to fetch home gifts from the server")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("Failed to fetch home gifts from the server")
        }

    }


    // 사용자 이메일을 기반으로 서버에서 기프트 목록을 가져오는 함수
    suspend fun fetchGiftListByEmail(userEmail: String): List<Home_gift> {
        var response: Response? = null
        try {
            // 사용자 이메일을 기반으로 홈 기프트 목록을 가져오기 위한 URL 생성
            val url = "http://3.35.110.246:3306/homeGifts_my?email=$userEmail"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val client = OkHttpClient.Builder()
                .build()

            response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // 서버 응답을 처리하고 MutableLiveData를 업데이트
                val jsonResponse = response.body?.string()
                Log.d("GiftAddRepository", "Server response: $jsonResponse")
                val homeGiftsList = parseHomeGifts(jsonResponse)
                _homeGifts.postValue(homeGiftsList)
                return homeGiftsList
            } else {
                // 서버 오류 처리
                val errorBody = response?.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("서버에서 데이터를 가져오지 못했습니다")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("서버에서 데이터를 가져오지 못했습니다")
        }
    }
}*/
