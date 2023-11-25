package com.example.giftimoa

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutHomeGiftEditBinding
import com.example.giftimoa.dto.Home_gift
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Home_gift_edit : AppCompatActivity() {
    private lateinit var gift: Home_gift
    private lateinit var giftViewModel: Gificon_ViewModel
    private lateinit var binding: LayoutHomeGiftEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutHomeGiftEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        // 툴바
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 정보 수정"

        // 카테고리와 브랜드 데이터
        val categories = arrayOf("--선택하세요--", "커피", "치킨", "피자", "패스트푸드", "편의점･마트", "베이커리", "아이스크림", "외식･분식", "영화･엔터", "기타")
        val brands = mapOf(
            "--선택하세요--" to arrayOf("-선택하세요-"),
            "커피" to arrayOf("스타벅스", "투썸플레이스", "메가커피", "이디야커피", "할리스커피", "빽다방", "컴포즈커피", "공차", "드롭탑", "커피빈"),
            "치킨" to arrayOf("BBQ", "BHC", "굽네치킨", "60계치킨", "교촌치킨", "처갓집", "바른치킨", "또래오래", "지코바", "기영이숯불두마리치킨", "페리카나", "꾸브라꼬숯불두마리치킨", "치킨플러스", "피자나라치킨공주"),
            "피자" to arrayOf("피자헛", "도미노피자", "미스터피자", "청년피자", "피자나라치킨공주", "반올림피자", "피자스쿨", "빽보이피자", "유로코피자", "파파존스", "피자마루", "샐러디", "에그드랍"),
            "패스트푸드" to arrayOf("맥도날드", "버거킹", "KFC", "롯데리아", "프랭크버거", "뉴욕버거", "맘스터치", "버거리", "이삭토스트", "테그42"),
            "편의점･마트" to arrayOf("GS25", "CU", "세븐일레븐", "미니스톱", "이마트", "이마트24", "롯데마트", "홈플러스", "하이마트", "GS슈퍼마켓(TheFRESH)"),
            "베이커리" to arrayOf("뚜레쥬르", "파리바게뜨", "던킨도너츠", "크리스피크림도넛", "한스제과", "파리크라상", "CJ", "해피콘", "홍루이젠", "와플대학"),
            "아이스크림" to arrayOf("베스킨라빈스", "설빙", "나뚜루", "해피콘"),
            "외식･분식" to arrayOf("아웃백 스테이크하우스", "빕스", "애슐리", "샐러디", "요기요", "배달의민족", "본죽", "죽이야기", "죠스떡볶이", "청년다방", "역전우동0410", "뉴욕야시장", "써브웨이", "생활맥주", "에그드랍"),
            "영화･엔터" to arrayOf("CGV", "롯데시네마", "메가박스", "컬쳐랜드", "해피머니", "구글플레이", "씨네큐", "인생네컷"),
            "기타" to arrayOf("올리브영", "CJ온스타일", "따릉이", "킥고일", "알파카"),
        )

        // 카테고리 스피너 어댑터 설정
        val categoryAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        binding.editSpinnerCategory.adapter = categoryAdapter

        // 카테고리 선택이 변경될 때마다 브랜드 스피너 업데이트
        binding.editSpinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCategory = categories[position]
                val brandAdapter = ArrayAdapter(this@Home_gift_edit, R.layout.simple_spinner_item, brands[selectedCategory]!!)
                binding.editSpinnerBrand.adapter = brandAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        binding.editEffectiveDate.setOnClickListener {
            showDatePickerDialog()
        }

        gift = intent.getSerializableExtra("gift") as Home_gift

        binding.apply {
            editGiftName.setText(gift.h_product_name)
            editEffectiveDate.setText(gift.h_effectiveDate)
            editPrice.setText(gift.h_price)

            val selectedCategory = gift.h_category
            val categoryAdapter = binding.editSpinnerCategory.adapter as? ArrayAdapter<String>
            if (categoryAdapter != null) {
                val indexc = categoryAdapter.getPosition(selectedCategory)
                binding.editSpinnerCategory.setSelection(indexc)
            } else {
                // 어댑터가 null인 경우, 적절한 처리를 해주어야 합니다.
                // 예: 새로운 어댑터를 생성하고 설정하는 등의 처리를 수행합니다.
            }

            val selectedBrand = gift.h_brand
            val brandAdapter = binding.editSpinnerBrand.adapter as? ArrayAdapter<String>
            if (brandAdapter != null) {
                val indexb = brandAdapter.getPosition(selectedBrand)
                binding.editSpinnerBrand.setSelection(indexb)
            } else {
                // 어댑터가 null인 경우, 적절한 처리를 해주어야 합니다.
                // 예: 새로운 어댑터를 생성하고 설정하는 등의 처리를 수행합니다.
            }

            editProductDescription.setText(gift.h_product_description)
            Glide.with(this@Home_gift_edit)
                .load(gift.h_imageUrl)
                .into(uploadImage)
        }

        binding.editBtn.setOnClickListener {
            // 입력 필드에서 수정된 정보 가져오기
            val newGiftName = binding.editGiftName.text.toString()
            val newEffectiveDate = binding.editEffectiveDate.text.toString()
            val newPrice = binding.editPrice.text.toString()
            val newCategory = binding.editSpinnerCategory.selectedItem.toString()
            val newBrand = binding.editSpinnerBrand.selectedItem.toString()
            val newProductDescription = binding.editProductDescription.text.toString()

            // 새로운 Home_gift 객체 생성
            val updatedGift = Home_gift(
                h_id = gift.h_id,
                h_product_name = newGiftName,
                h_effectiveDate = newEffectiveDate,
                h_price = newPrice,
                h_category = newCategory,
                h_brand = newBrand,
                h_product_description = newProductDescription,
                h_imageUrl = gift.h_imageUrl,
                h_state = gift.h_state,
                favorite = gift.favorite,
                nickname = gift.nickname
            )

            // ViewModel에 Home_gift 객체 업데이트
            giftViewModel.Home_updateGift(updatedGift)

            // 서버에 수정된 정보 업데이트
            lifecycleScope.launch {
                Log.d("kimjyeongki", "$updatedGift")
                updateGiftOnServer(updatedGift)
            }

            // 수정된 결과를 액티비티에 전달
            val intent = Intent()
            intent.putExtra("updatedGift", updatedGift)
            setResult(Activity.RESULT_OK, intent)

            // 이전 화면으로 돌아가기
            finish()
        }
    }

    private suspend fun updateGiftOnServer(updatedGift: Home_gift) {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                // 서버에 보낼 JSON 데이터 생성
                val json = """
                {
                    "h_product_name": "${updatedGift.h_product_name}",
                    "h_effectiveDate": "${updatedGift.h_effectiveDate}",
                    "h_price": "${updatedGift.h_price}",
                    "h_category": "${updatedGift.h_category}",
                    "h_brand": "${updatedGift.h_brand}",
                    "h_product_description": "${updatedGift.h_product_description}",
                    "h_imageUrl": "${updatedGift.h_imageUrl}"
                }
            """.trimIndent()

                // 서버 업데이트 요청
                val requestBody = json.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("http://3.35.110.246:3306/update_home_gift/${updatedGift.h_id}")
                    .put(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                Log.d("kimjyeongki1", "${updatedGift.h_id}")

                if (response.isSuccessful) {
                    // 서버 업데이트 성공
                    // 원하는 작업 수행
                } else {
                    // 서버 업데이트 실패
                    // 원하는 작업 수행
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // 예외 처리
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            // 현재 날짜와 선택한 날짜를 비교
            if (selectedDate.before(calendar)) {
                // 선택한 날짜가 현재 날짜보다 이전이면 다이얼로그를 표시하고 effectiveDate를 비웁니다.
                val builder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(com.example.giftimoa.R.layout.dialog_gifticon_date_cancle, null)
                builder.setView(dialogLayout)
                val dialog = builder.create()
                dialog.setOnShowListener {
                    val okButton = dialog.findViewById<TextView>(com.example.giftimoa.R.id.btn_ok)
                    okButton?.setOnClickListener {
                        dialog.dismiss()
                        binding.editEffectiveDate.setText("")
                    }
                }
                dialog.show()
            } else {
                // 선택한 날짜가 현재 날짜보다 이후이면 effectiveDate를 설정합니다.
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                binding.editEffectiveDate.setText(date)
            }
        }, year, month, day).show()
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}