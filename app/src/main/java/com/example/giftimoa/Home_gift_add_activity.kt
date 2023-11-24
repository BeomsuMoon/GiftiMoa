package com.example.giftimoa

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutHomeGiftAddBinding
import com.example.giftimoa.dto.Home_gift
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.suspendCoroutine

class Home_gift_add_activity : AppCompatActivity() {
    private lateinit var binding : LayoutHomeGiftAddBinding

    private lateinit var giftViewModel: Gificon_ViewModel
    private val REQUEST_READ_EXTERNAL_STORAGE = 1001
    private var h_imageUrl: String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutHomeGiftAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 등록"

        // 카테고리와 브랜드 데이터
        val categories = arrayOf("--선택하세요--","커피","치킨", "피자", "패스트푸드","편의점･마트","베이커리","아이스크림","외식･분식","영화･엔터","기타")
        val brands = mapOf(
            "--선택하세요--" to arrayOf("-선택하세요-"),
            "커피" to arrayOf("스타벅스", "투썸플레이스", "메가커피", "이디야커피", "할리스커피", "빽다방", "컴포즈커피", "공차", "드롭탑", "커피빈"),
            "치킨" to arrayOf("BBQ", "BHC", "굽네치킨","60계치킨","교촌치킨","처갓집","바른치킨","또래오래","지코바","기영이숯불두마리치킨","페리카나","꾸브라꼬숯불두마리치킨","치킨플러스","피자나라치킨공주"),
            "피자" to arrayOf("피자헛", "도미노피자","미스터피자","청년피자","피자나라치킨공주","반올림피자","피자스쿨","빽보이피자","유로코피자","파파존스","피자마루","샐러디","에그드랍"),
            "패스트푸드" to arrayOf("맥도날드", "버거킹","KFC","롯데리아","프랭크버거","뉴욕버거","맘스터치","버거리","이삭토스트","테그42"),
            "편의점･마트" to arrayOf("GS25", "CU","세븐일레븐","미니스톱","이마트","이마트24","롯데마트","홈플러스","하이마트","GS슈퍼마켓(TheFRESH)"),
            "베이커리" to arrayOf("뚜레쥬르", "파리바게뜨","던킨도너츠","크리스피크림도넛","한스제과","파리크라상","CJ","해피콘","홍루이젠","와플대학"),
            "아이스크림" to arrayOf("베스킨라빈스", "설빙","나뚜루","해피콘"),
            "외식･분식" to arrayOf("아웃백 스테이크하우스", "빕스","애슐리","샐러디","요기요","배달의민족","본죽","죽이야기","죠스떡볶이","청년다방","역전우동0410","뉴욕야시장","써브웨이","생활맥주","에그드랍"),
            "영화･엔터" to arrayOf("CGV","롯데시네마","메가박스","컬쳐랜드","해피머니","구글플레이","씨네큐","인생네컷"),
            "기타" to arrayOf("올리브영","CJ온스타일","따릉이","킥고일","알파카"),
        )

        // 카테고리 스피너 어댑터 설정
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        binding.spinnerCategory.adapter = categoryAdapter

        // 카테고리 선택이 변경될 때마다 브랜드 스피너 업데이트
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCategory = categories[position]
                val brandAdapter = ArrayAdapter(this@Home_gift_add_activity, android.R.layout.simple_spinner_item, brands[selectedCategory]!!)
                binding.spinnerBrand.adapter = brandAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        galleryClickEvent()

        binding.fullscreenBtn.setOnClickListener {
            showFullscreenImageDialog(h_imageUrl)
        }
        binding.textEffectiveDate.setOnClickListener {
            showDatePickerDialog()
        }

        giftAdd_Btn()
    }
    private fun galleryClickEvent(){
        binding.uploadImage.setOnClickListener ({
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                //권한이 허용되지 않음
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ){
                    //이전에 이미 권한이 거부되었을 때 설명
                    var dlg = AlertDialog.Builder(this)
                    dlg.setTitle("권한이 필요한 이유")
                    dlg.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                    dlg.setPositiveButton("확인") { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@Home_gift_add_activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    dlg.setNegativeButton("취소", null)
                    dlg.show()
                }else {
                    //권한 요청
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            } else {
                // If imageUrl is not empty show the image in fullscreen, else load image
                loadImage()
            }
        })
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResult.launch(intent)
    }
    //갤러리 호출
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        if(it.resultCode == RESULT_OK && it.data != null){
            val uri = it.data!!.data

            Glide.with(this)
                .load(uri)
                .into(binding.uploadImage)

            // 이미지 URI를 문자열로 변환하여 저장
            h_imageUrl = uri.toString()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getNicknameFromServer(userEmail: String?): String = suspendCoroutine { continuation ->
        val client = OkHttpClient()
        val url1 = "http://3.35.110.246:3306/getNicknameByEmail"

        if (userEmail.isNullOrBlank()) {
            continuation.resumeWith(Result.success(""))
        } else {
            val json = """{"email": "$userEmail"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url1)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Home_gift_add_act", "Failed to get nickname from server")
                    continuation.resumeWith(Result.success(""))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val nickname = responseData?.replace("\"", "") ?: ""
                        continuation.resumeWith(Result.success(nickname))
                    } else {
                        Log.e("Home_gift_add_act", "Failed to get nickname from server")
                        continuation.resumeWith(Result.success(""))
                    }
                }
            })
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun giftAdd() {
        val sharedPreferences = this.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "") ?: ""

        var h_product_name = binding.textGiftName.text.toString()
        var h_effectiveDate = binding.textEffectiveDate.text.toString()
        var h_price = binding.textPrice.text.toString()
        var h_category = binding.spinnerCategory.selectedItem.toString()
        var h_brand = binding.spinnerBrand.selectedItem.toString()
        var h_product_description = binding.textProductDescription.text.toString()

        try {
            if (h_product_name.isEmpty() || h_effectiveDate.isEmpty() || h_price.isEmpty() || h_category.isEmpty() || h_brand.isEmpty() || h_imageUrl.isEmpty() || h_product_description.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val id = UUID.randomUUID().hashCode()

                        val nickname = withContext(Dispatchers.IO) { getNicknameFromServer(userEmail) }
                        Log.d("test", "$nickname")

                        val homeGift = Home_gift(id, h_product_name, h_effectiveDate, h_price, h_category, h_brand, h_product_description, h_imageUrl, 0, 0, nickname)

                        homeGift.h_state = Home_Utils.calState(homeGift)  // state 값에 calState의 결과를 할당
                        val resultIntent = Intent()
                        resultIntent.putExtra("gift", homeGift)
                        setResult(Activity.RESULT_OK, resultIntent)

                        val url = "http://3.35.110.246:3306/home_gift_add"
                        val json = JsonObject().apply {
                            addProperty("h_product_name", h_product_name)
                            addProperty("h_effectiveDate", h_effectiveDate)
                            addProperty("h_price", h_price)
                            addProperty("h_category", h_category)
                            addProperty("h_brand", h_brand)
                            addProperty("h_product_description", h_product_description)
                            addProperty("h_imageUrl", h_imageUrl)
                            addProperty("h_state", 0)
                            addProperty("favorite", 0)
                            if (userEmail.isNotEmpty()) {
                                Log.d("userEmail", "$userEmail")
                                addProperty("userEmail", userEmail)
                            }
                            addProperty("nickname", nickname)
                        }

                        val mediaType = "application/json; charset=utf-8".toMediaType()
                        val requestBody = json.toString().toRequestBody(mediaType)
                        val request = Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build()

                        try {
                            val client = OkHttpClient()
                            val response: Response = withContext(Dispatchers.IO) { client.newCall(request).execute() }

                            if (response.isSuccessful) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@Home_gift_add_activity,
                                        "DB 정보 입력 성공",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            } else {
                                runOnUiThread {
                                    val errorBody = response.body?.string()
                                    Log.d("Home_gift_test:", "$errorBody")
                                    Toast.makeText(
                                        this@Home_gift_add_activity,
                                        "DB 정보 입력 실패: $errorBody",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("Home_gift_add_act", "DB 에러: ${e.message}", e)
                            runOnUiThread {
                                Toast.makeText(
                                    this@Home_gift_add_activity,
                                    "오류 발생: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace() // 로깅을 위해 사용하지 않음
                        Log.e("Home_gift_add_act", "에러: ${e.message}", e)  // 예외 정보를 Log.e로 출력
                        runOnUiThread {
                            Toast.makeText(
                                this@Home_gift_add_activity,
                                "오류 발생: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } finally {
                        finish()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // 로깅을 위해 사용하지 않음
            Log.e("Home_gift_add_act", "에러: ${e.message}", e)  // 예외 정보를 Log.e로 출력
            runOnUiThread {
                Toast.makeText(
                    this@Home_gift_add_activity,
                    "오류 발생: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
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
                val dialogLayout = inflater.inflate(R.layout.dialog_gifticon_date_cancle, null)
                builder.setView(dialogLayout)
                val dialog = builder.create()
                dialog.setOnShowListener {
                    val okButton = dialog.findViewById<TextView>(R.id.btn_ok)
                    okButton?.setOnClickListener {
                        dialog.dismiss()
                        binding.textEffectiveDate.setText("")
                    }
                }
                dialog.show()
            } else {
                // 선택한 날짜가 현재 날짜보다 이후이면 effectiveDate를 설정합니다.
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                binding.textEffectiveDate.setText(date)
            }
        }, year, month, day).show()
    }

    private fun showFullscreenImageDialog(h_imageUrl: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_image, null)
        val dialogImage = dialogLayout.findViewById<ImageView>(R.id.dialog_image)

        Glide.with(this)
            .load(h_imageUrl)
            .into(dialogImage)

        val dialog = builder.setView(dialogLayout)
            .create()

        dialogImage.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun giftAdd_Btn() {
        binding.addBtn.setOnClickListener {
            giftAdd()
        }
    }
}