package com.example.giftimoa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.ChatItem
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketHandler {
    private var socket: Socket? = null
    private var _onNewChat = MutableLiveData<ChatItem>()
    val onNewChatItem:LiveData<ChatItem> get() = _onNewChat
    init{
        try {
            socket = IO.socket(SOCKET_URL)
            socket?.connect()

            registerOnNewChat()
        }catch (e:URISyntaxException){
            e.printStackTrace()
        }

    }

    private fun registerOnNewChat() {
        socket?.on(CHAT_KEYS.NEW_MESSAGE){
            it?.let { d->
                if(d.toString().isNotEmpty()){
                    val chat =Gson().fromJson(d.toString(), ChatItem::class.java)
                    _onNewChat.postValue(chat)
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
    }
    companion object{
        private const val SOCKET_URL = "http://10.0.2.2:3000/"
    }
}