package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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

        binding.textGiftName.text = gift.giftName
        binding.textEffectiveDate.text = gift.effectiveDate
        binding.textBarcode.text = gift.barcode
        binding.textUsage.text = gift.usage

        Glide.with(this)
            .load(gift.imageUrl)
            .into(binding.uploadImage)

        //이미지 클릭시 이미지 전체 화면 보기
        binding.uploadImage.setOnClickListener {
            val intent = Intent(this, FullScreenImage_Activity::class.java)
            intent.putExtra("imageUrl", gift.imageUrl)
/*            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)*/
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
