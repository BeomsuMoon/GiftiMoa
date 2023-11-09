package com.example.giftimoa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.giftimoa.databinding.LayoutFullscreenImageBinding
import com.example.giftimoa.dto.Collect_Gift

class FullScreenImage_Activity : AppCompatActivity() {
    private lateinit var binding: LayoutFullscreenImageBinding
    private lateinit var gift: Collect_Gift

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 정보"

        val imageUrl = intent.getStringExtra("imageUrl")

        Glide.with(this)
            .load(gift.imageUrl)
/*            .apply(
                RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .signature(ObjectKey(System.currentTimeMillis())))*/
            .into(binding.uploadImage)  // uploadImageView를 실제 이미지뷰 ID로 변경하세요.
    }
    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}