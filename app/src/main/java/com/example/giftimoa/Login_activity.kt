package com.example.giftimoa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess

class Login_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_login)

        // XML에서 정의한 위젯들과 연결
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val Sign_up = findViewById<TextView>(R.id.Login_Sign_up)
        val Search_ID = findViewById<TextView>(R.id.Login_ID_Find)
        val Search_PW = findViewById<TextView>(R.id.Login_PW_Find)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // ID_Find_activity에서 넘겨받은 이메일 값이 있는지 확인하고, 있다면 이메일 입력 필드에 설정
        val receivedEmail = intent.getStringExtra("email_text")
        if (!receivedEmail.isNullOrEmpty()) {
            emailEditText.setText(receivedEmail)
        }

        // 로그인 버튼 클릭 시 이벤트 처리
        loginButton.setOnClickListener(View.OnClickListener {
            // 입력된 이메일과 비밀번호 가져오기
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // 서버 URL 설정
            val serverUrl = "http://3.35.110.246:3306/login_node?email=$email&password=$password"

            // HTTP GET 요청 보내기
            Thread {
                try {
                    val url = URL(serverUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"

                    // 서버 응답 코드 확인
                    val responseCode = connection.responseCode

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 서버 응답을 읽어올 BufferedReader 생성
                        val reader = BufferedReader(InputStreamReader(connection.inputStream))
                        val response = StringBuilder()
                        var line: String?

                        // 응답 데이터를 읽어서 response에 저장
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }

                        // 응답 처리 (로그인 성공 또는 실패에 따라 다른 처리)
                        val jsonResponse = response.toString()
                        try {
                            val jsonObject = JSONObject(jsonResponse)
                            val result = jsonObject.getInt("result")

                            if (result == 0) {

                                // SharedPreferences에 email 저장
                                val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("user_email", email)
                                editor.apply()

                                // 로그인 성공 시 다음 화면으로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else if (result == 1) {
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "이메일 또는 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "서버 오류 또는 알 수 없는 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            runOnUiThread {
                                Toast.makeText(applicationContext, "응답 데이터 파싱 오류", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // 서버 응답이 실패인 경우
                        runOnUiThread {
                            Toast.makeText(applicationContext, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(applicationContext, "오류 발생: " + e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        })

        // 회원가입 버튼 클릭 이벤트
        Sign_up.setOnClickListener {
            val intent = Intent(this, SignUpemail_activity::class.java)
            startActivity(intent)
        }

        Search_ID.setOnClickListener {
            val intent = Intent(this, ID_Find_activity::class.java)
            startActivity(intent)
        }

        Search_PW.setOnClickListener {
            val intent = Intent(this, PW_Find_activity::class.java)
            startActivity(intent)
        }
    }

    private var backPressedTime: Long = 0

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > 2000) {
            Toast.makeText(this, "한 번 더 누르면 앱이 종료됩니다..", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        } else {
            exitApp() // 앱 종료 함수 호출
        }
    }

    private fun exitApp() {
        finish() // 현재 액티비티 종료
        moveTaskToBack(true) // 태스크를 백그라운드로 이동
        android.os.Process.killProcess(android.os.Process.myPid()) // 프로세스 종료
        exitProcess(1) // 시스템 종료
    }
}