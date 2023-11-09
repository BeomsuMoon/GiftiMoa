package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutCollectGiftAddBinding
import com.example.giftimoa.databinding.LayoutSearchIdBinding
import com.example.giftimoa.databinding.LayoutSearchPwBinding

class PW_Find_activity : AppCompatActivity() {
    private lateinit var binding : LayoutSearchPwBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSearchPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nextpw_btn()
    }

    private fun nextpw_btn(){
        binding.nextViewBtn.setOnClickListener {
            val intent = Intent(this@PW_Find_activity, PW_Find_Result_activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }
}