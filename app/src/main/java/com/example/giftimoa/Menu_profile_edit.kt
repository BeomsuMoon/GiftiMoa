package com.example.giftimoa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutMenuProfileEditBinding

class Menu_profile_edit : AppCompatActivity() {

    private lateinit var binding: LayoutMenuProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 사용
        binding = LayoutMenuProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
