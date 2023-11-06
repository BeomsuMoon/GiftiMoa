package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
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

class SignUp_activity : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var nicknameEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sign_up)

        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title = "회원가입"  // 액션바 제목 설정

        val signUpButton: Button = findViewById(R.id.signup_button)
        emailEditText = findViewById(R.id.ID_textField_editText)
        passwordEditText = findViewById(R.id.PW_textField_editText)
        confirmPasswordEditText = findViewById(R.id.PW_Re_textField_editText)
        nameEditText = findViewById(R.id.username_editText)
        phoneNumberEditText = findViewById(R.id.user_phone_number_editText)
        nicknameEditText = findViewById(R.id.user_nickname_editText)

        // 닉네임 중복 확인 버튼 클릭 이벤트 처리
        val nicknameDoubleCheckButton: Button = findViewById(R.id.nickname_double_check)
        nicknameDoubleCheckButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()

            // "nickname double-check"을 위한 서버 URL로 대체하십시오
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
                                Toast.makeText(this@SignUp_activity, "이 닉네임은 이미 사용 중입니다.", Toast.LENGTH_SHORT).show()
                            } else if (status == "중복 아님") {
                                Toast.makeText(this@SignUp_activity, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                                nicknameEditText.isEnabled = false
                                nicknameEditText.isFocusable = false
                                nicknameEditText.isFocusableInTouchMode = false
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SignUp_activity, "서버 오류", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@SignUp_activity, "오류 발생${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 핸드폰 번호 중복 확인 버튼 클릭 이벤트 처리
        val phoneDoubleCheckButton: Button = findViewById(R.id.phone_double_check)
        phoneDoubleCheckButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString()

            val url = "http://3.35.110.246:3306/checkPhoneNumber" // 핸드폰 번호 중복 체크
            val json = JsonObject().apply {
                addProperty("phone_number", phoneNumber)
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
                                Toast.makeText(this@SignUp_activity, "이 번호는 이미 사용 중입니다.", Toast.LENGTH_SHORT).show()
                            } else if (status == "중복 아님") {
                                Toast.makeText(this@SignUp_activity, "사용 가능한 번호입니다.", Toast.LENGTH_SHORT).show()
                                phoneNumberEditText.isEnabled = false
                                phoneNumberEditText.isFocusable = false
                                phoneNumberEditText.isFocusableInTouchMode = false
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SignUp_activity, "서버 오류", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@SignUp_activity, "오류 발생${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val receivedEmail = intent.getStringExtra("email_text")
        if (receivedEmail != null) {
            emailEditText.setText(receivedEmail)
        }
        setUseableEditText(emailEditText, false)  // 이메일 입력 필드를 비활성화

        signUpButton.setOnClickListener {
            if (validateForm()) {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val name = nameEditText.text.toString()
                val phoneNumber = phoneNumberEditText.text.toString()
                val username = nicknameEditText.text.toString()

                val url = "http://3.35.110.246:3306/signup3"
                val json = JsonObject().apply {
                    addProperty("email", email)
                    addProperty("password", password)
                    addProperty("phone_number", phoneNumber)
                    addProperty("name", name)
                    addProperty("username", username)
                }
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                // 비동기 네트워크 요청을 위해 CoroutineScope 사용
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response: Response = withContext(Dispatchers.IO) {
                            OkHttpClient().newCall(request).execute()
                        }

                        if (response.isSuccessful) {
                            val intent = Intent(this@SignUp_activity, Login_activity::class.java)
                            startActivity(intent)
                            runOnUiThread {
                                Toast.makeText(this@SignUp_activity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@SignUp_activity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@SignUp_activity, "오류 발생", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setUseableEditText(et: EditText, useable: Boolean) {
        et.isClickable = useable
        et.isEnabled = useable
        et.isFocusable = useable
        et.isFocusableInTouchMode = useable
    }

    // 양식 유효성 검사를 수행하는 메서드
    private fun validateForm(): Boolean {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val name = nameEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        val username = nicknameEditText.text.toString()

        if (!isValidEmail(email)) {
            emailEditText.error = "올바른 이메일을 입력하세요."
            return false
        } else {
            emailEditText.error = null
        }

        if (password.length < 8 || !isValidPassword(password)) {
            passwordEditText.error = "비밀번호는 8자 이상이어야 하며, 영어, 숫자, 특수 문자를 포함해야 합니다."
            return false
        } else {
            passwordEditText.error = null
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "비밀번호가 일치하지 않습니다."
            return false
        } else {
            confirmPasswordEditText.error = null
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            phoneNumberEditText.error = "올바른 핸드폰 번호를 입력하세요."
            return false
        } else {
            phoneNumberEditText.error = null
        }
        if (name.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "빈칸을 입력하세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // 이메일 유효성 검사를 수행하는 메서드
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
        return email.matches(emailPattern.toRegex())
    }

    // 비밀번호 유효성 검사를 수행하는 메서드
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#\$%^&+=]).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    // 핸드폰 번호 유효성 검사를 수행하는 메서드
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneNumberPattern = "^01[0-9]{8,9}$"
        return phoneNumber.matches(phoneNumberPattern.toRegex())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
