package com.example.giftimoa.dto

import android.provider.ContactsContract.CommonDataKinds.Nickname
import java.io.Serializable
import java.time.LocalDateTime

data class Home_gift(
    val h_id: Int,
    var h_product_name: String, //상품명
    var h_effectiveDate: String, //유효기간
    var h_price: String, //가격
    var h_category: String, // 카테고리
    var h_brand: String, //사용처
    var h_product_description: String,
    var h_imageUrl: String, //이미지 URI
    var h_state: Int, //유효기간 상태 체크
    var favorite: Int, //나의 관심 상품 체크
    var nickname: String
) : Serializable



data class H_Badge(
    val h_content: String,
    val h_color: String
)