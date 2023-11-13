package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutSearchIdBinding
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
import com.google.gson.JsonParser

class ID_Find_activity : AppCompatActivity() {
    private lateinit var binding: LayoutSearchIdBinding
    private var foundEmail: String = "" // 글로벌로 사용할 이메일 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSearchIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val findButton: Button = findViewById(R.id.find_button)
        val loginButton: Button = findViewById(R.id.login_Button)
        val phoneEditText: EditText = findViewById(R.id.phone_id)
        val emailTextView: TextView = findViewById(R.id.email_text)

        findButton.setOnClickListener {
            val phoneNumber = phoneEditText.text.toString()

            // 전화번호 유효성 체크
            if (isValidPhoneNumber(phoneNumber)) {
                // 서버에서 아이디 찾기 로직 수행
                val url = "http://3.35.110.246:3306/idFind"  // 서버 URL로 대체
                val json = JsonObject().apply {
                    addProperty("phoneNumber", phoneNumber)  // 서버 요청 파라미터 이름을 서버에서 기대하는 이름으로 변경
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

                            runOnUiThread {
                                // 서버 응답에서 "error"가 없으면 이메일을 가져와서 표시
                                if (!jsonObject.has("error")) {
                                    foundEmail = jsonObject.get("email").asString // 이메일 값을 글로벌 변수에 저장
                                    emailTextView.text = "찾은 이메일: $foundEmail"
                                    Toast.makeText(this@ID_Find_activity, "아이디 찾기에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    emailTextView.isEnabled = false
                                    emailTextView.isFocusable = false
                                } else {
                                    val errorMessage = jsonObject.get("message").asString
                                    Toast.makeText(this@ID_Find_activity, errorMessage, Toast.LENGTH_SHORT).show()
                                    emailTextView.text = ""
                                }
                            }
                        } else {
                            runOnUiThread {
                                // Handle unsuccessful response...
                                Toast.makeText(this@ID_Find_activity, "아이디 찾기에 실패 하였습니다.", Toast.LENGTH_SHORT).show()
                                emailTextView.text = ""
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@ID_Find_activity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@ID_Find_activity, "유효하지 않은 전화번호입니다.", Toast.LENGTH_SHORT).show()
                emailTextView.text = ""
            }
        }

        loginButton.setOnClickListener {
            // TextView에서 이메일 가져오기
            val email = foundEmail

            if (email.isNotEmpty()) {
                // Login_activity를 시작하는 Intent 생성
                val intent = Intent(this@ID_Find_activity, Login_activity::class.java)
                // 이메일을 인텐트의 추가 데이터로 넣기
                intent.putExtra("email_text", email)
                // Login_activity 시작
                startActivity(intent)
            } else {
                val intent = Intent(this@ID_Find_activity, Login_activity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneNumberPattern = "^01[0-9]{8,9}$"
        return phoneNumber.matches(phoneNumberPattern.toRegex())
    }
}