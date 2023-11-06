package com.example.giftimoa.dto

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class Collect_Gift(
    @SerializedName("gift_img") //쿠폰명
    var img : Int,
    @SerializedName("gift_name") //쿠폰명
    var giftName : String,
    @SerializedName("gift_date") //유효기간
    var date: String,
    @SerializedName("gift_barcode") //바코드
    var barcode: String,
    @SerializedName("gift_brnad") //사용처
    var brand: String,
    /*    @SerializedName("available") //사용여부
        var available: Boolean*/
)