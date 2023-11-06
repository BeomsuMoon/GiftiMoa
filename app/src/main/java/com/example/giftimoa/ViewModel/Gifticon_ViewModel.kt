package com.example.giftimoa.ViewModel

import androidx.lifecycle.ViewModel
import com.example.giftimoa.dto.Collect_Gift

class Gifticon_ViewModel : ViewModel() {
    var giftName: String = ""
    var date: String = ""
    var barcode: String = ""
    var brand: String = ""

    // 이 메서드를 사용하여 데이터를 설정할 수 있습니다.
    fun setGiftData(giftName: String, date: String, barcode: String, brand: String) {
        this.giftName = giftName
        this.date = date
        this.barcode = barcode
        this.brand = brand
    }

    // 이 메서드를 사용하여 데이터를 가져올 수 있습니다.
    fun getGiftData(): Collect_Gift {
        return Collect_Gift(giftName, date, barcode, brand)
    }
}