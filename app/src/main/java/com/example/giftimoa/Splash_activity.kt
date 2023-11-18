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


        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this,Login_activity::class.java)
            startActivity(intent)
            finish()
        },DULATION)
    }
    companion object{
        private const val DULATION : Long = 1500
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}