package com.example.giftimoa.repository


import com.example.giftimoa.dto.ChatRoom
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class ChatRepository {
    private val client = OkHttpClient()
    suspend fun getChatRooms(nickname: String): List<ChatRoom>? {
        Log.d("ChatRepository", "getChatRooms is called with nickname: $nickname")
        val url = "http://3.35.110.246:3306/chatList?nickname=$nickname"
        val request = Request.Builder().url(url).build()
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
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

                @Throws(JsonParseException::class)
                override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
                    return df.parse(json.asString)
                }
            })
            .create()
        val type = object : TypeToken<List<ChatRoom>>() {}.type
        return gson.fromJson(json, type)
    }
}
