package com.example.giftimoa

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.giftimoa.databinding.LayoutCollectGiftAddBinding
import com.example.giftimoa.databinding.LayoutSearchIdBinding
import android.widget.EditText // EditText를 사용하기 위해 추가
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class PhoneRequest(val phoneNumber: String)
data class EmailResponse(val email: String)

interface ApiService {
    @POST("/findEmailByPhoneNumber")
    fun findEmailByPhoneNumber(@Body request: PhoneRequest): Call<EmailResponse>
}

class ID_Find_activity : AppCompatActivity() {
    private lateinit var binding: LayoutSearchIdBinding
    private lateinit var email_text: EditText
    private lateinit var phone_id: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSearchIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        email_text = findViewById(R.id.email_text)
        phone_id = findViewById(R.id.phone_id)

        val find_button = findViewById<Button>(R.id.find_button)

        find_button.setOnClickListener {
            val phoneNumber = phone_id.text.toString()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://3.35.110.246") // 서버의 기본 URL로 변경
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            val request = PhoneRequest(phoneNumber)
            val call = apiService.findEmailByPhoneNumber(request)

            call.enqueue(object : Callback<EmailResponse> {
                override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                    if (response.isSuccessful) {
                        val email = response.body()?.email
                        if (!email.isNullOrBlank()) {
                            email_text.setText(email)
                        }
                    } else {
                        // 서버 요청 실패 또는 응답 처리에 실패한 경우에 대한 처리
                    }
                }

                override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                    // 네트워크 오류 또는 예외 처리
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

