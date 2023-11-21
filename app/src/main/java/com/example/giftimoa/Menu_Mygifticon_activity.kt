package com.example.giftimoa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giftimoa.adpater_list.MyGifticonAdapter
import com.example.giftimoa.databinding.LayoutMenuMygifticonBinding
import com.example.giftimoa.dto.Home_gift
import com.example.giftimoa.repository.GiftAddRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Menu_Mygifticon_activity : AppCompatActivity() {

    private lateinit var binding: LayoutMenuMygifticonBinding
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
        giftAdapter = MyGifticonAdapter(mutableListOf()) { selectedGift ->
            // 기프트를 클릭했을 때 수행할 동작
            // 여기서는 예시로 주석만 달았습니다. 실제 동작은 개발자의 요구사항에 따라 추가되어야 합니다.
            // 예: 기프트 상세 화면으로 이동하는 코드를 여기에 추가할 수 있습니다.
        }
        binding.rvFavorite.layoutManager = LinearLayoutManager(this)
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
