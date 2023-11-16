package com.example.giftimoa

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.adpater_list.RecyclerViewHomeGiftAdapter
import com.example.giftimoa.databinding.LayoutMenuFavoriteListBinding

class Menu_favorite_activity : AppCompatActivity() {
    private lateinit var binding: LayoutMenuFavoriteListBinding
    private lateinit var giftViewModel: Gificon_ViewModel

    private val giftAdapter = RecyclerViewHomeGiftAdapter(mutableListOf()) { gift ->
        // 이 부분에 아이템 클릭 시 수행할 동작을 작성하세요.
        // 예를 들어, Toast 메시지를 띄우는 코드를 작성할 수 있습니다.
        Toast.makeText(this, "${gift.h_product_name} clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMenuFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        //액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "찜한 기프티콘"

        setupRecyclerView()

        // 찜한 기프티콘 목록을 불러옵니다.
        giftViewModel.getFavoriteGifts().observe(this, { gifts ->
            // gifts는 찜한 기프티콘 리스트입니다.
            giftAdapter.setGiftList(gifts.toMutableList())
        })
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = layoutManager
        binding.rvFavorite.adapter = giftAdapter
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
