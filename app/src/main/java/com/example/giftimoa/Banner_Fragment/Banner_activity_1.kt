package com.example.giftimoa.Banner_Fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.R
import com.example.giftimoa.databinding.BannerGuide1Binding

class Banner_activity_1 : AppCompatActivity() {

    private lateinit var binding: BannerGuide1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 사용
        binding = BannerGuide1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
