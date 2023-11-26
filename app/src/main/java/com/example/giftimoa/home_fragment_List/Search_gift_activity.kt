package com.example.giftimoa.home_fragment_List

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.example.giftimoa.R
import com.example.giftimoa.adpater_list.HomeFragment_adpater_list

class Search_gift_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_search_gift) // 사용할 레이아웃 파일

        //툴바
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "브랜드"

        // ViewPager2와 어댑터 초기화
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val adapter = HomeFragment_adpater_list(this)
        viewPager.adapter = adapter

        // TabLayout 초기화
        val tabLayout: TabLayout = findViewById(R.id.tabs)

        // TabLayout과 ViewPager2를 연결하여 탭과 페이지 간의 연동을 설정
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // 각 탭의 이름을 설정
            tab.text = when (position) {
                0 -> "커피"
                1 -> "치킨"
                2 -> "피자"
                3 -> "패스트푸드"
                4 -> "편의점･마트"
                5 -> "베이커리"
                6 -> "아이스크림"
                7 -> "외식･분식"
                8 -> "영화･엔터"
                else -> "기타"
            }
        }.attach()

        // 페이지 변경 시 액션바의 타이틀 업데이트
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                supportActionBar?.title = when (position) {
                    0 -> "커피"
                    1 -> "치킨"
                    2 -> "피자"
                    3 -> "패스트푸드"
                    4 -> "편의점･마트"
                    5 -> "베이커리"
                    6 -> "아이스크림"
                    7 -> "외식･분식"
                    8 -> "영화･엔터"
                    else -> "기타"
                }
            }
        })

        // Intent로부터 tabIndex를 가져옴
        val tabIndex = intent.getIntExtra("tabIndex", 0)
        viewPager.currentItem = tabIndex
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

