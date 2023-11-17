package com.example.giftimoa.bottom_nav_fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.giftimoa.Login_activity
import com.example.giftimoa.Menu_Mygifticon_activity
import com.example.giftimoa.Menu_favorite_activity
import com.example.giftimoa.R
import com.example.giftimoa.databinding.FragmentMenuBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class Menu_Fragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences에서 email 값 불러오기
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "")

        // 서버에서 닉네임 가져오기
        getNicknameFromServer(userEmail)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // userEmail 값을 tv_account에 설정
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "")
        binding.root.findViewById<TextView>(R.id.tv_account).text = userEmail

        // userEmail 값을 tv_account에 설정
        binding.root.findViewById<TextView>(R.id.tv_account).text = userEmail


        //마감임박 최초 알림
        binding.lNotiFirst.setOnClickListener {

        }

        //마감임박 알림 간격
        binding.lNotiInterval.setOnClickListener {

        }
        binding.lNotiTime.setOnClickListener {

        }

        //나의 관심 기프티콘
        binding.tvFavorite.setOnClickListener {
            val intent = Intent(requireContext(), Menu_favorite_activity::class.java)
            startActivity(intent)
        }

        //내 상품 관리
        binding.myGifticon.setOnClickListener {
            val intent = Intent(requireContext(), Menu_Mygifticon_activity::class.java)
            startActivity(intent)
        }


        //회원탈퇴
        binding.tvWithdraw.setOnClickListener {

        }

        //로그아웃
        binding.tvLogout.setOnClickListener {
            // SharedPreferences에서 이메일 삭제
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("user_email")
            editor.apply()

            // LoginActivity 시작
            val intent = Intent(requireContext(), Login_activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        binding.switchNoti.setOnCheckedChangeListener { _, isChecked ->

        }
    }
    private fun getNicknameFromServer(userEmail: String?) {
        val client = OkHttpClient()
        val url = "http://3.35.110.246:3306/getNicknameByEmail" // 서버의 닉네임 확인 엔드포인트

        // 이메일이 null이 아니면 서버에 요청을 보냄
        if (!userEmail.isNullOrBlank()) {
            val json = """{"email": "$userEmail"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val responseData = response.body?.string()
                    activity?.runOnUiThread {
                        updateNicknameInView(responseData)
                    }
                }
            })
        }
    }

    private fun updateNicknameInView(nickname: String?) {
        if (!nickname.isNullOrBlank()) {
            val welcomeMessage = "$nickname" + "님 환영합니다."
            binding.root.findViewById<TextView>(R.id.tv_title_account).text = welcomeMessage
        }
    }

}
