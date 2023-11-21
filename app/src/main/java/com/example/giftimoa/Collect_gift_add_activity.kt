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
import android.widget.Button
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
import com.example.giftimoa.databinding.LayoutCollectGiftAddBinding
import com.example.giftimoa.dto.Collect_Gift
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class Collect_gift_add_activity : AppCompatActivity() {
    private lateinit var binding: LayoutCollectGiftAddBinding

    private lateinit var giftViewModel: Gificon_ViewModel
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private var imageUrl: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutCollectGiftAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        // 액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 등록"

        // 갤러리 이미지 클릭 이벤트 설정
        galleryClickEvent()

        // 효력 날짜 선택 다이얼로그 표시
        binding.textEffectiveDate.setOnClickListener {
            showDatePickerDialog()
        }

        // 기프트 추가 버튼 클릭 이벤트 설정
        giftAdd_Btn()
    }

    // 이미지 클릭시 갤러리 권한 요청 및 처리
    private fun galleryClickEvent() {
        binding.uploadImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 허용되지 않음
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    // 이전에 이미 권한이 거부되었을 때 설명
                    var dlg = AlertDialog.Builder(this)
                    dlg.setTitle("권한이 필요한 이유")
                    dlg.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                    dlg.setPositiveButton("확인") { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@Collect_gift_add_activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    dlg.setNegativeButton("취소", null)
                    dlg.show()
                } else {
                    // 권한 요청
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            } else {
                // 이미지 URL이 비어 있지 않으면 이미지를 풀 스크린으로 표시하고, 그렇지 않으면 이미지를 로드합니다.
                if (imageUrl.isNotEmpty()) {
                    showFullscreenImageDialog(imageUrl)
                } else {
                    loadImage()
                }
            }
        }
    }

    // 갤러리 호출
    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResult.launch(intent)
    }

    // 갤러리에서 선택한 이미지를 처리하는 결과 콜백
    private val activityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val uri = it.data!!.data

                Glide.with(this)
                    .load(uri)
                    .into(binding.uploadImage)

                // 이미지 URI를 문자열로 변환하여 저장
                imageUrl = uri.toString()
            }
        }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun giftAdd() {
        val sharedPreferences = this.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", null)

        var giftName = binding.textGiftName.text.toString()
        var effectiveDate = binding.textEffectiveDate.text.toString()
        var barcode = binding.textBarcode.text.toString()
        var usage = binding.textUsage.text.toString()

        try {
            if (giftName.isEmpty() || effectiveDate.isEmpty() || barcode.isEmpty() || usage.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val hash_id = UUID.randomUUID().hashCode()
                val collectGift =
                    Collect_Gift(hash_id, giftName, effectiveDate, barcode, usage, imageUrl, 0)

                collectGift.state =
                    Collect_Utils.calState(collectGift)  // state 값에 calState의 결과를 할당
                val resultIntent = Intent()
                resultIntent.putExtra("gift", collectGift)
                setResult(Activity.RESULT_OK, resultIntent)

                val url = "http://3.35.110.246:3306/collect_gift_add"
                val json = JsonObject().apply {
                    addProperty("giftName", giftName)
                    addProperty("effectiveDate", effectiveDate)
                    addProperty("barcode", barcode)
                    addProperty("usage", usage)
                    addProperty("imageUrl", imageUrl)
                    addProperty("state", 0)
                    if (userEmail != null && userEmail.isNotEmpty()) {
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
                                    this@Collect_gift_add_activity,
                                    "DB 정보 입력 성공",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            runOnUiThread {
                                val errorBody = response.body?.string()
                                Toast.makeText(
                                    this@Collect_gift_add_activity,
                                    "DB 정보 입력 실패: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Collect_gift_add_act", "DB 에러: ${e.message}", e)
                        runOnUiThread {
                            Toast.makeText(
                                this@Collect_gift_add_activity,
                                "오류 발생: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace() // 로깅을 위해 사용하지 않음
            Log.e("Collect_gift_add_act", "에러: ${e.message}", e)  // 예외 정보를 Log.e로 출력
            runOnUiThread {
                Toast.makeText(
                    this@Collect_gift_add_activity,
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
                // 선택한 날짜가 현재 날짜보다 이전이면 다이얼로그를 표시하고 effectiveDate는 변경하지 않습니다.
                val builder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.dialog_gifticon_date_cancle, null)
                builder.setView(dialogLayout)
                val dialog = builder.create()
                dialog.setOnShowListener {
                    val okButton = dialog.findViewById<TextView>(R.id.btn_ok)
                    okButton?.setOnClickListener {
                        dialog.dismiss()
                    }
                }
                dialog.show()
            } else {
                // 선택한 날짜가 현재 날짜보다 이후이면 effectiveDate를 설정합니다.
                val date =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                binding.textEffectiveDate.setText(date)
            }
        }, year, month, day).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun giftAdd_Btn() {
        binding.addBtn.setOnClickListener {
            giftAdd()
        }
    }


    private fun showFullscreenImageDialog(imageUrl: String) {
        // AlertDialog.Builder 인스턴스 생성
        val builder = AlertDialog.Builder(this)

        // LayoutInflater를 통해 커스텀 레이아웃(dialog_image.xml)을 인플레이트
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_image, null)

        // ImageView를 dialogLayout에서 찾아옴
        val dialogImage = dialogLayout.findViewById<ImageView>(R.id.dialog_image)

        // Glide를 사용하여 imageUrl에 해당하는 이미지를 ImageView에 로드
        Glide.with(this)
            .load(imageUrl)
            .into(dialogImage)

        // AlertDialog를 생성하고 커스텀 레이아웃을 설정
        val dialog = builder.setView(dialogLayout)
            .create()

        // 이미지를 클릭하면 다이얼로그를 닫도록 설정
        dialogImage.setOnClickListener {
            dialog.dismiss()
        }

        // 다이얼로그의 배경을 검은색으로 설정
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // 다이얼로그 표시
        dialog.show()
    }
}
