package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import android.util.Log  // 추가: Log 사용을 위한 import 문
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutSearchPwResultBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import android.widget.Toast

class PW_Find_Result_activity : AppCompatActivity() {
    private lateinit var binding: LayoutSearchPwResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSearchPwResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent에서 이메일 값을 가져옴
        val email = intent.getStringExtra("email_text")
        // 이메일 값을 이메일 입력 칸에 설정
        binding.emailEditText.setText(email)
        // EditText를 비활성화
        binding.emailEditText.isEnabled = false

        nextPwBtn()
        cancelBtn()
    }

    private fun passwordCheck(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#\$%^&+=]).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun nextPwBtn() {
        binding.nextViewBtn.setOnClickListener {
            // 사용자 입력에서 새 비밀번호 가져오기
            val newPassword = binding.passwordEditText.text.toString()
            val confirmPassword = binding.certificationNum.text.toString()

            // 비밀번호 유효성 체크
            if (passwordCheck(newPassword)) {
                // 비밀번호가 일치하는지 확인
                if (newPassword == confirmPassword) {
                    // 서버의 데이터베이스에서 비밀번호 업데이트
                    updatePasswordOnServer(newPassword)
                } else {
                    Toast.makeText(this@PW_Find_Result_activity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@PW_Find_Result_activity, "비밀번호는 8자 이상이어야 하며, 영어, 숫자, 특수 문자를 포함해야 합니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun updatePasswordOnServer(newPassword: String) {
        val email = intent.getStringExtra("email_text")

        // 서버에 비밀번호를 업데이트하도록 요청
        val url = "http://3.35.110.246:3306/updatePassword"
        val json = JsonObject().apply {
            addProperty("email", email)
            addProperty("newPassword", newPassword)
        }

        val mediaType = "application/json".toMediaType()
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

                runOnUiThread {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val jsonObject = JsonParser().parse(responseData).asJsonObject

                        // 서버 응답을 필요에 따라 처리
                        handleServerResponse(jsonObject)
                    } else {
                        // 실패한 응답 처리...
                        Toast.makeText(
                            this@PW_Find_Result_activity,
                            "비밀번호 업데이트에 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("PW_Find_Result_activity", "에러: ${e.message}")  // 추가: 예외 로그 출력
                    Toast.makeText(
                        this@PW_Find_Result_activity,
                        "오류 발생: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleServerResponse(jsonObject: JsonObject) {
        // 서버 응답을 필요에 따라 처리
        val message = jsonObject.get("message").asString
        if (message == "비밀번호가 성공적으로 업데이트되었습니다.") {
            Toast.makeText(
                this@PW_Find_Result_activity,
                "비밀번호가 성공적으로 변경 되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this@PW_Find_Result_activity, Login_activity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료

        } else {
            val errorMessage = jsonObject.get("message").asString
            Toast.makeText(this@PW_Find_Result_activity, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelBtn() {
        binding.cancelButton.setOnClickListener {
            // 로그인 화면으로 이동
            val intent = Intent(this@PW_Find_Result_activity, Login_activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }
}