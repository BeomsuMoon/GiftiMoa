package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giftimoa.adpater_list.RecyclerViewHomeGiftAdapter
import com.example.giftimoa.databinding.LayoutMenuFavoriteListBinding
import com.example.giftimoa.dto.favorite

class Menu_favorite_activity : AppCompatActivity() {
    private lateinit var binding: LayoutMenuFavoriteListBinding

    private val giftAdapter = RecyclerViewHomeGiftAdapter(mutableListOf()) { gift ->
        val intent = Intent(this, Home_gift_add_info_activity::class.java)
        intent.putExtra("gift", gift)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMenuFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "찜한 기프티콘"

        setupRecyclerView()

        // favoriteGifts 리스트를 RecyclerView에 추가합니다.
        giftAdapter.setGiftList(favorite.FavoriteGifts.list)
    }

    private fun setupRecyclerView() {
        // GridLayoutManager에서 두 번째 인자는 열의 수를 지정합니다.
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvFavorite.layoutManager = layoutManager
        binding.rvFavorite.adapter = giftAdapter
    }


    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
