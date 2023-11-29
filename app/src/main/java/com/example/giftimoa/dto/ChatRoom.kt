package com.example.giftimoa.dto

import java.time.LocalDate

data class ChatRoom(
    val id: Int,
    val nickname: String,
    val last_message: String,
    val last_date: LocalDate
    )
