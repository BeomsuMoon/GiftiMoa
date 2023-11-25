package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.adpater_list.MyGifticonAdapter
import com.example.giftimoa.adpater_list.RecyclerViewCollectGiftAdapter
import com.example.giftimoa.adpater_list.RecyclerViewHomeGiftAdapter
import com.example.giftimoa.databinding.LayoutMenuMygifticonBinding
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.dto.Home_gift
import com.example.giftimoa.repository.GiftAddRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Menu_Mygifticon_activity : AppCompatActivity() {

    private lateinit var binding: LayoutMenuMygifticonBinding
    private lateinit var gift: Home_gift
    private lateinit var giftAdapter: MyGifticonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩 초기화
        binding = LayoutMenuMygifticonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "내 기프티콘"
        // 사용자 이메일 가져오기
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "")

        // RecyclerView 설정
        giftAdapter = MyGifticonAdapter(mutableListOf()) { gift ->
            val intent = Intent(this@Menu_Mygifticon_activity, Home_gift_add_myinfo_activity::class.java)
            intent.putExtra("gift", gift) // 선택한 기프트 정보를 인텐트에 추가
            startActivity(intent)
        }
        binding.rvFavorite.layoutManager = GridLayoutManager(this, 2)
        binding.rvFavorite.adapter = giftAdapter

        // 데이터 가져오기
        getUserGifts(userEmail)
    }

    private fun getUserGifts(userEmail: String?) {
        // 코루틴을 사용하여 백그라운드에서 데이터 가져오기
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Repository에서 데이터 가져오기
                val repository = GiftAddRepository(this@Menu_Mygifticon_activity)
                val homeGiftsList = repository.fetchGiftListByEmail(userEmail ?: "")

                // UI 스레드에서 데이터 업데이트
                withContext(Dispatchers.Main) {
                    giftAdapter.setMyGiftList(homeGiftsList)
                }
            } catch (e: Exception) {
                // 예외 처리
                e.printStackTrace()
                // 여기서 UI 스레드에서 예외 처리를 수행할 수 있습니다.
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
