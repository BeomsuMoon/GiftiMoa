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

    private val _onChatroomCreated = MutableLiveData<List<ChatRoom>>()
    val roomCreated: LiveData<List<ChatRoom>> get() = _onChatroomCreated

    init {
        try {
            socket = IO.socket(SOCKET_URL)
            socket?.connect()
            registerOnNewChat()
/*            registerOnChatroomCreated()*/

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

/*    private fun registerOnChatroomCreated() {
        socket?.on("chatroom_created") { args ->
            val chatroomJson = (args[0] as JSONObject).toString()
            Log.d("SocketHandler", "Received data from server: $chatroomJson")
            val chatroom = Gson().fromJson(chatroomJson, ChatRoom::class.java)
            Log.d("SocketHandler", "Converted data to ChatRoom: $chatroom")
             _onChatroomCreated.postValue(listOf(chatroom))
            Log.d("SocketHandler", "Posted data to LiveData: $chatroom")
        }
    }*/

    private fun registerOnNewChat() {
        socket?.on(CHAT_KEYS.BROADCAST){ args->
            args?.let { d->
                if(d.isNotEmpty()){
                    val data = d[0]
                    Log.d("DATADEBUG","$data")
                    if(d.toString().isNotEmpty()) {
                        val chat = Gson().fromJson(data.toString(), ChatItem::class.java)
                        _onNewChat.postValue(chat)
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
}
