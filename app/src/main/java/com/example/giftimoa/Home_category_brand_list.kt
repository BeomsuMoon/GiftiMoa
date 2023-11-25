package com.example.giftimoa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.adpater_list.RecyclerViewCategoryBrandAdapter
import com.example.giftimoa.adpater_list.RecyclerViewHomeGiftAdapter
import com.example.giftimoa.databinding.BannerGuide1Binding
import com.example.giftimoa.databinding.LayoutCategoryBrandBinding
import com.example.giftimoa.dto.Home_gift

class Home_category_brand_list : AppCompatActivity() {

    private lateinit var binding: LayoutCategoryBrandBinding
    private lateinit var giftViewModel: Gificon_ViewModel
    private lateinit var adapter: RecyclerViewCategoryBrandAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 사용
        binding = LayoutCategoryBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.myToolbar)
        val brandName = intent.getStringExtra("brand_name") ?: ""
        supportActionBar?.title = brandName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ViewModel 인스턴스 가져오기
        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        // 브랜드 별 상품 정보 가져오기
        giftViewModel.fetchBrandGifts(brandName)

        // 어댑터 초기화
        adapter = RecyclerViewCategoryBrandAdapter(mutableListOf()) {
            // 아이템 클릭 시 동작을 정의하는 부분
            // 예) startActivity, showDialog 등
        }
        binding.rvCategoryBrand.adapter = adapter
        binding.rvCategoryBrand.layoutManager = GridLayoutManager(this, 2)

        // ViewModel의 LiveData 관찰
        giftViewModel.homeGifts.observe(this) { gifts ->
            // 어댑터에 데이터 업데이트
            adapter.setMyGiftList(gifts)
        }
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
