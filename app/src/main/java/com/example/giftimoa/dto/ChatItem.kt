package com.example.giftimoa.dto

data class ChatItem(
    val id: Int,
    val nickname: String,
    val reciver_nickname: String,
    val brand: String,
    val message: String,
    val timestamp: String,
)