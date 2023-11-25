package com.example.giftimoa.dto

data class ChatItem(
    //val profileImageUrl: String,
    //val nickname: String,
    val message: String,
    val timestamp: Long,
    val isDate: Boolean = false
)
