/*
package com.example.giftimoa

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.ChatItem
import com.example.giftimoa.dto.ChatRoom
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class SocketHandler {
    private var socket: Socket? = null
    private var _onNewChat = MutableLiveData<ChatItem>()
    val onNewChatItem: LiveData<ChatItem> get() = _onNewChat

    init {
        try {
            socket = IO.socket(SOCKET_URL)
            socket?.connect()
            registerOnNewChat()

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun registerOnNewChat() {
        socket?.on(CHAT_KEYS.BROADCAST) { args ->
            args?.let { d ->
                if (d.isNotEmpty()) {
                    try {
                        val jsonObject = JSONObject(d[0].toString())
                        val chat = Gson().fromJson(jsonObject.getString("chat"), ChatItem::class.java)
                        _onNewChat.postValue(chat)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("ChattingRoom", "Error parsing chat JSON", e)
                    }
                }
            }
        }
    }

    fun disconnectSocket(){
        socket?.disconnect()
        socket?.off()
    }

    fun emitChat(chat: ChatItem){
        val jsonStr = Gson().toJson(chat,ChatItem::class.java)
        socket?.emit(CHAT_KEYS.NEW_MESSAGE, jsonStr)
    }

    private object CHAT_KEYS {
        const val NEW_MESSAGE = "new_message"
        const val BROADCAST = "broadcast"
    }

    companion object{
        private const val SOCKET_URL = "http://3.35.110.246:3306/"
    }
}*/
