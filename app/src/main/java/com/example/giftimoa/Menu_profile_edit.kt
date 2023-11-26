package com.example.giftimoa

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.giftimoa.bottom_nav_fragment.Menu_Fragment
import com.example.giftimoa.databinding.LayoutMenuProfileEditBinding
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import androidx.fragment.app.Fragment


class Menu_profile_edit : AppCompatActivity() {

    private lateinit var binding: LayoutMenuProfileEditBinding
    private val REQUEST_READ_EXTERNAL_STORAGE = 1003
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 사용
        binding = LayoutMenuProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 닉네임을 인텐트에서 가져와 TextInputLayout에 설정
        val nicknameWithDot = intent.getStringExtra("nickname")
        val nickname = nicknameWithDot?.removeSuffix(".") ?: ""

        binding.textInputLayout.editText?.setText(nickname)

        val nicknameDoubleCheckButton: Button = findViewById(R.id.nickname_check_button)
        nicknameDoubleCheckButton.setOnClickListener {
            val nickname = binding.textInputLayout.editText?.text.toString()

            //닉네임 체크 엔드포인트
            val url = "http://3.35.110.246:3306/checkNickName"

            val json = JsonObject().apply {
                addProperty("username", nickname) // "nickname" 대신 "username"으로 변경
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: Response = withContext(Dispatchers.IO) {
                        OkHttpClient().newCall(request).execute()
                    }
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val jsonObject = JsonParser().parse(responseData).asJsonObject
                        val status = jsonObject.get("status").asString

                        runOnUiThread {
                            if (status == "중복") {
                                Toast.makeText(this@Menu_profile_edit, "이 닉네임은 이미 사용 중입니다.", Toast.LENGTH_SHORT).show()
                            } else if (status == "중복 아님") {
                                Toast.makeText(this@Menu_profile_edit, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                                binding.textInputLayout.isEnabled = false
                                binding.textInputLayout.isFocusable = false
                                binding.textInputLayout.isFocusableInTouchMode = false
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@Menu_profile_edit, "서버 오류", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@Menu_profile_edit, "오류 발생${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val complete_Button: Button = findViewById(R.id.complete_btn)
        complete_Button.setOnClickListener {
            // 이메일 값을 가져옴
            val userEmail = getUserEmailFromSharedPreferences()

            if (userEmail != null) {
                val url = "http://3.35.110.246:3306/profile_update"

                // 사용자가 입력한 닉네임을 가져옴
                val nickname = binding.textInputLayout.editText?.text.toString()

                // 이미지 URL과 닉네임을 서버에 업데이트하는 요청을 생성
                val json = JsonObject().apply {
                    addProperty("email", userEmail)
                    addProperty("username", nickname)
                    addProperty("Profile_picture", imageUrl)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response: Response = withContext(Dispatchers.IO) {
                            OkHttpClient().newCall(request).execute()
                        }

                        if (response.isSuccessful) {
                            // 응답이 성공적인 경우 Menu_Fragment로 이동
                            runOnUiThread {
                                val intent = Intent(this@Menu_profile_edit, Menu_Fragment::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            // 응답이 실패한 경우 토스트 메시지 표시
                            runOnUiThread {
                                Toast.makeText(this@Menu_profile_edit, "프로필 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@Menu_profile_edit, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // 이메일이 없는 경우에 대한 처리
                Toast.makeText(this@Menu_profile_edit, "이메일 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        setUpProfileImageClickEvent()
    }

    // SharedPreferences에서 이메일을 가져오는 함수
    private fun getUserEmailFromSharedPreferences(): String? {
        val sharedPreferences = this.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_email", null)
    }
    private fun setUpProfileImageClickEvent() {
        binding.userProfile.setOnClickListener {
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
                    val dlg = AlertDialog.Builder(this)
                    dlg.setTitle("권한이 필요한 이유")
                    dlg.setMessage("프로필 이미지를 변경하기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                    dlg.setPositiveButton("확인") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this@Menu_profile_edit,
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
                loadImage()

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
                    .into(binding.userProfile)

                // 이미지 URI를 문자열로 변환하여 저장
                imageUrl = uri.toString()
            }
        }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
