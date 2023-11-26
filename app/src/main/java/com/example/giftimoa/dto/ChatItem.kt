package com.example.giftimoa.dto

data class ChatItem(
    //val profileImageUrl: String,
    val nickname: String,
    val message: String,
    val timestamp: Long,
    val isSelf: Boolean = false,
    val isDate: Boolean = false
)
