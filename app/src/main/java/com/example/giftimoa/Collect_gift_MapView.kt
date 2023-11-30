package com.example.giftimoa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutCollectGiftMapviewBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import net.daum.mf.map.api.MapView

class Collect_gift_MapView : AppCompatActivity() {

    private lateinit var binding: LayoutCollectGiftMapviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 사용
        binding = LayoutCollectGiftMapviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ConstraintLayout에 MapView 추가
        val mapView = MapView(this)
        binding.mapView.addView((mapView))



    }


    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
