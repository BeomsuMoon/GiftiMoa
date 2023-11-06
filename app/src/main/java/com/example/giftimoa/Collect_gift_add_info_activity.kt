package com.example.giftimoa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutCollectGiftAddBinding
import com.example.giftimoa.databinding.LayoutCollectGiftAddInfoBinding

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

    }

    var gift_Name = intent.getStringExtra("gift_Name")
    var gift_effectiveDate = intent.getStringExtra("gift_effectiveDate")
    var gift_barcode = intent.getStringExtra("gift_barcode")
    var gift_usage = intent.getStringExtra("gift_usage")
    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }

}