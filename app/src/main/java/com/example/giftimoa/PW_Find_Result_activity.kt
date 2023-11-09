package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutSearchPwResultBinding

class PW_Find_Result_activity : AppCompatActivity() {
    private lateinit var binding : LayoutSearchPwResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSearchPwResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nextpw_btn()
        cancle_btn()
    }
    private fun nextpw_btn(){
        binding.nextViewBtn.setOnClickListener {
            val intent = Intent(this@PW_Find_Result_activity, Login_activity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun cancle_btn(){
        binding.cancelButton.setOnClickListener {
            val intent = Intent(this@PW_Find_Result_activity, Login_activity::class.java)
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