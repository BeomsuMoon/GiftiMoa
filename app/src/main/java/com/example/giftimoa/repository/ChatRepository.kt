package com.example.giftimoa.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.ChatRoom
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import android.util.Log
import com.example.giftimoa.dto.Home_gift
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException


class ChatRepository {
    private val client = OkHttpClient()

    // MutableLiveData를 사용하여 홈 기프트 목록을 보관
    private val _chatroomList: MutableLiveData<List<ChatRoom>> = MutableLiveData()
    val chatroomList: LiveData<List<ChatRoom>>
        get() = _chatroomList

    suspend fun getChatRooms(nickname: String): List<ChatRoom>? {
        Log.d("ChatRepository", "getChatRooms is called with nickname: $nickname")
        val url = "http://3.35.110.246:3306/chatList?nickname=$nickname"
        val request = Request
            .Builder()
            .url(url)
            .build()

        return try {
            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()
                    parseChatRoomsFromJson(responseData)
                }
            }
        } catch (e: Exception) {
            null
        }
    }


    private fun parseChatRoomsFromJson(json: String?): List<ChatRoom> {
        val gson = Gson()
        val type = object : TypeToken<List<ChatRoom>>() {}.type
        return gson.fromJson(json, type)
    }
}
