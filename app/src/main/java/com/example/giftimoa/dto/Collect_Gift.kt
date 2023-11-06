package com.example.giftimoa.dto

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class Collect_Gift(
    var giftName : String,
    var date: String,
    var barcode: String,
    var brand: String,

)