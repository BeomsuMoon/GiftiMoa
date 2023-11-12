package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutHomeGiftAddInfoBinding
import com.example.giftimoa.dto.Home_gift

class Home_gift_add_info_activity : AppCompatActivity() {
    private lateinit var binding : LayoutHomeGiftAddInfoBinding
    private lateinit var gift: Home_gift
    private lateinit var giftViewModel: Gificon_ViewModel

    private val editActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedGift = result.data?.getSerializableExtra("updatedGift") as? Home_gift
            if (updatedGift != null) {
                gift = updatedGift
                // 여기에서 updatedGift를 화면에 업데이트합니다.
                binding.textGiftName.text = gift.h_product_name
                binding.textEffectiveDate.text = gift.h_effectiveDate
                binding.textPrice.text = gift.h_price
                binding.textExpiration.text = gift.h_brand
                binding.textProductDescription.text = gift.h_product_description
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutHomeGiftAddInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        //액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 정보"

        gift = intent.getSerializableExtra("gift") as Home_gift

        binding.textGiftName.text = gift.h_product_name
        binding.textEffectiveDate.text = gift.h_effectiveDate
        binding.textPrice.text = gift.h_price
        binding.textExpiration.text = gift.h_brand
        binding.textProductDescription.text = gift.h_product_description

        Glide.with(this)
            .load(gift.h_imageUrl)
            .into(binding.uploadImage)

        //이미지 클릭시 이미지 전체 화면 보기
        binding.uploadImage.setOnClickListener {
            val intent = Intent(this, FullScreenImage_Activity::class.java)
            intent.putExtra("imageUrl", gift.h_imageUrl)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }

}