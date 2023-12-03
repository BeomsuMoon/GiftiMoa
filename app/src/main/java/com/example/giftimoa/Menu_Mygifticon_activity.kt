package com.example.giftimoa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.adpater_list.MyGifticonAdapter
import com.example.giftimoa.databinding.LayoutMenuMygifticonBinding
import com.example.giftimoa.repository.GiftAddRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Menu_Mygifticon_activity : AppCompatActivity() {
    private lateinit var binding: LayoutMenuMygifticonBinding
    private lateinit var giftAdapter: MyGifticonAdapter

    // ViewModel 초기화
    private val giftViewModel: Gificon_ViewModel by lazy {
        ViewModelProvider(this).get(Gificon_ViewModel::class.java)
    }
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

    override fun onResume() {
        super.onResume()

        // 코루틴을 사용하여 fetchGiftListFromRepository 호출
        lifecycleScope.launch {
            val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("user_email", null)
            if (userEmail != null && userEmail.isNotEmpty()) {
                // giftViewModel 사용
                giftViewModel.fetchHomeGiftListFromRepository(applicationContext, userEmail)
                // 데이터 가져오고 나서 RecyclerView 업데이트
                getUserGifts(userEmail)
            } else {
                Log.d("tests", "tests : $userEmail")
            }
        }
    }

    // 외부에서 직접 호출할 수 있도록 합니다.
    fun getUserGifts(userEmail: String?) {
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