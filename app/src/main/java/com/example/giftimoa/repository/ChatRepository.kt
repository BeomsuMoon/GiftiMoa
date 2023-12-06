package com.example.giftimoa.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.ChatItem
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class ChatRepository {
    private val _chatMessages = MutableLiveData<List<ChatItem>>()
    val chatMessages: LiveData<List<ChatItem>> get() = _chatMessages
    private val client = OkHttpClient()

    suspend fun fetchChatMessages(nickname: String) {
        withContext(Dispatchers.IO) {
            val url = "http://3.35.110.246:3306/message?nickname=$nickname"
            val request = Request.Builder().url(url).build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val messages = Gson().fromJson(responseData, Array<ChatItem>::class.java).toList()

                        _chatMessages.postValue(messages)
                    } else {
                        // Handle unsuccessful response here
                        Log.e("ChatRepository", "Unsuccessful response: ${response.code}")
                        // 예외를 던지거나 적절한 처리를 수행합니다.
                    }
                }
            } catch (e: IOException) {
                // Handle IO exception here
                e.printStackTrace()
                // 예외를 던지거나 적절한 처리를 수행합니다.
            }
        }
    }

    suspend fun sendMessage(nickname: String, receiverNickname: String, brand: String, message: String, timestamp: String) {
        withContext(Dispatchers.IO) {
            val url = "http://3.35.110.246:3306/chatmessage_add"
            val json = """{
                "nickname": "$nickname",
                "reciver_nickname": "$receiverNickname",
                "brand": "$brand",
                "message": "$message",
                "timestamp": "$timestamp"
            }"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                }
            } catch (e: IOException) {
                // Handle IO exception here
                e.printStackTrace()
                // 예외를 던지거나 적절한 처리를 수행합니다.
            }
        }
    }
}