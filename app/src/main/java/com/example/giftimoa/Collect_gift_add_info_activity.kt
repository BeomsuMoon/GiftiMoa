package com.example.giftimoa

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutCollectGiftAddBinding
import com.example.giftimoa.databinding.LayoutCollectGiftAddInfoBinding
import com.example.giftimoa.dto.Collect_Gift

class Collect_gift_add_info_activity : AppCompatActivity() {
    private lateinit var binding : LayoutCollectGiftAddInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutCollectGiftAddInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 정보"

        val gift = intent.getSerializableExtra("gift") as Collect_Gift
        var giftNameTextView = binding.textGiftName
        var textEffectiveDateTextView = binding.textEffectiveDate
        var textBarcodeTextView = binding.textBarcode
        var textUsageTextView = binding.textUsage

        giftNameTextView.text = gift.giftName
        textEffectiveDateTextView.text = gift.effectiveDate
        textBarcodeTextView.text = gift.barcode
        textUsageTextView.text = gift.usage

    }


    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }

}