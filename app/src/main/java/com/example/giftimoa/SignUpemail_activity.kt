package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SignUpemail_activity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var certificationNum: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sign_up_email)

        emailEditText = findViewById(R.id.email_editText)
        certificationNum = findViewById(R.id.certification_num)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.email_double_check).setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isNotEmpty()) {
                // 이메일 중복 확인 요청
                checkEmailDuplicate(email)
            } else {
                Toast.makeText(this, "이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.nextView_btn).setOnClickListener {
            val enteredVerificationCode = certificationNum.text.toString()

            if (enteredVerificationCode.isNotEmpty()) {
                // 인증 코드 확인 요청
                verifyVerificationCode(emailEditText.text.toString(), enteredVerificationCode)
            } else {
                Toast.makeText(this, "인증 코드를 입력해주세요..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmailDuplicate(email: String) {
        val client = OkHttpClient()
        val url = "http://3.35.110.246:3306/checkDuplicateEmail" // 서버의 중복 확인 엔드포인트
        val json = """{"email": "$email"}"""
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SignUpemail_activity, "중복 확인 요청 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string()
                val jsonResponse = JSONObject(responseText)

                if (response.isSuccessful) {
                    val message = jsonResponse.getString("message")
                    runOnUiThread {
                        Toast.makeText(this@SignUpemail_activity, message, Toast.LENGTH_SHORT).show()

                        if (message == "이메일은 사용 가능하며 인증 코드를 이메일로 전송했습니다.") {
                            // 중복 확인이 성공한 경우, 이메일 입력 필드를 비활성화
                            emailEditText.isEnabled = false
                            emailEditText.isFocusable = false

                        }
                    }
                } else {
                    val error = jsonResponse.getString("error")
                    runOnUiThread {
                        Toast.makeText(this@SignUpemail_activity, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun verifyVerificationCode(email: String, enteredCode: String) {
        val client = OkHttpClient()
        val url = "http://3.35.110.246:3306/verify_verification_code" // 서버의 인증 코드 확인 엔드포인트
        val json = """{"email": "$email", "verCode": "$enteredCode"}"""
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SignUpemail_activity, "인증 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string()
                val jsonResponse = JSONObject(responseText)

                if (response.isSuccessful) {
                    val message = jsonResponse.getString("message")
                    runOnUiThread {
                        Toast.makeText(this@SignUpemail_activity, message, Toast.LENGTH_SHORT).show()

                        if (message == "Verification successful") {
                            // 인증 성공 시, 다음 단계로 이동 (예: SignUpActivity)
                            val intent = Intent(this@SignUpemail_activity, SignUp_activity::class.java)
                            intent.putExtra("email_text", email)
                            startActivity(intent)
                            finish() // 현재 액티비티를 종료하고 SignUp_activity로 이동
                        }
                    }
                } else {
                    val error = jsonResponse.getString("error")
                    runOnUiThread {
                        Toast.makeText(this@SignUpemail_activity, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}