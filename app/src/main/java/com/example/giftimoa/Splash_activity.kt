package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Splash_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_splash)

        Handler().postDelayed({
            val intent = Intent(this,Login_activity::class.java)
            startActivity(intent)
            finish()
        },DULATION)
    }
    companion object{
        private const val DULATION : Long = 1500
    }
}