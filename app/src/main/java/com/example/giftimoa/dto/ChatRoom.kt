package com.example.giftimoa.dto

data class ChatRoom(
    val id: Int,
    val nickname: String,
    val reciver_nickname: String?,
    val last_message: String,
    val last_date: Long
    )
