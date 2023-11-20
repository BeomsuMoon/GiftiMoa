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
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutHomeGiftAddBinding
import com.example.giftimoa.dto.Home_gift
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

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

        galleryClickEvent()

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
                if (h_imageUrl.isNotEmpty()) {
                    showFullscreenImageDialog(h_imageUrl)
                } else {
                    loadImage()
                }
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
    private fun giftAdd() {

        val sharedPreferences = this.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "") ?: ""


        var h_product_name = binding.textGiftName.text.toString()
        var h_effectiveDate = binding.textEffectiveDate.text.toString()
        var h_price = binding.textPrice.text.toString()
        var h_brand = binding.textExpiration.text.toString()
        var h_product_description = binding.textProductDescription.text.toString()


        try {
            if (h_product_name.isEmpty() || h_effectiveDate.isEmpty() || h_price.isEmpty() || h_brand.isEmpty() || h_imageUrl.isEmpty() || h_product_description.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val id = UUID.randomUUID().hashCode()
                val homeGift =
                    Home_gift(id, h_product_name, h_effectiveDate, h_price, h_brand, h_product_description, h_imageUrl, 0, 0,)


                homeGift.h_state = Home_Utils.calState(homeGift)  // state 값에 calState의 결과를 할당
                val resultIntent = Intent()
                resultIntent.putExtra("gift", homeGift)
                setResult(Activity.RESULT_OK, resultIntent)

                val url = "http://3.35.110.246:3306/home_gift_add"
                val json = JsonObject().apply {
                    addProperty("h_product_name", h_product_name)
                    addProperty("h_effectiveDate", h_effectiveDate)
                    addProperty("h_price", h_price)
                    addProperty("h_brand", h_brand)
                    addProperty("h_product_description", h_product_description)
                    addProperty("h_imageUrl", h_imageUrl)
                    addProperty("h_state", 0)
                    addProperty("favorite", 0)
                    if (userEmail != null && userEmail.isNotEmpty()) {
                        Log.d("userEmail","$userEmail")
                        addProperty("userEmail", userEmail)
                    }
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val client = OkHttpClient()
                        val response: Response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@Home_gift_add_activity,
                                    "DB 정보 입력 성공",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            runOnUiThread {
                                val errorBody = response.body?.string()
                                Log.d("Hoom_gift_test:", "$errorBody")
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
                }

                finish()
            }
        }catch (e: Exception) {
            e.printStackTrace() // 로깅을 위해 사용하지 않음
            Log.e("Home_gift_add_act", "에러: ${e.message}", e)  // 예외 정보를 Log.e로 출력
            runOnUiThread {
                Toast.makeText(
                    this@Home_gift_add_activity,
                    "오류 발생: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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