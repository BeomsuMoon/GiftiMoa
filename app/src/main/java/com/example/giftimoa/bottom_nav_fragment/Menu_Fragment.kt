package com.example.giftimoa.bottom_nav_fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.giftimoa.AlarmReceiver
import com.example.giftimoa.Login_activity
import com.example.giftimoa.Menu_Mygifticon_activity
import com.example.giftimoa.Menu_favorite_activity
import com.example.giftimoa.R
import com.example.giftimoa.databinding.DialogNumpickBinding
import com.example.giftimoa.databinding.DialogYcBtnBinding
import com.example.giftimoa.databinding.FragmentMenuBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Calendar

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

        // 초기 상태 설정 (스위치를 활성 상태로 설정)
        binding.switchNoti.isChecked = true
        // 스위치의 체크 상태 변경 리스너 설정
        binding.switchNoti.setOnCheckedChangeListener { _, isChecked ->
            // 스위치가 체크되어 있으면 lNotiFirst, lNotiInterval, lNotiTime 클릭 가능
            binding.lNotiFirst.isClickable = isChecked
            binding.lNotiInterval.isClickable = isChecked
            binding.lNotiTime.isClickable = isChecked

            // 스위치가 체크되어 있지 않으면 lNotiFirst, lNotiInterval, lNotiTime 클릭 불가능
            if (!isChecked) {
                binding.lNotiFirst.setOnClickListener(null)
                binding.lNotiInterval.setOnClickListener(null)
                binding.lNotiTime.setOnClickListener(null)
                binding.lNotiAlram.setOnClickListener(null)

                binding.tvNotiSettingFirst.setTextColor(Color.parseColor("#939393"))
                binding.tvNotiSettingFirstText.setTextColor(Color.parseColor("#939393"))
                binding.tvNotiTitleFirst.setTextColor(Color.parseColor("#939393"))

                binding.tvNotiSettingInterval.setTextColor(Color.parseColor("#939393"))
                binding.tvNotiSettingIntervalText.setTextColor(Color.parseColor("#939393"))
                binding.tvNotiTitleInterval.setTextColor(Color.parseColor("#939393"))

                binding.tvNotiSettingTime.setTextColor(Color.parseColor("#939393"))
                binding.tvNotiTitleTime.setTextColor(Color.parseColor("#939393"))


            } else {
                binding.lNotiFirst.setOnClickListener { showNumberPickerFirstDialog() }
                binding.lNotiInterval.setOnClickListener { showNumberPickerAlramDialog() }
                binding.lNotiTime.setOnClickListener { showTimePickerDialog() }

                binding.tvNotiSettingFirst.setTextColor(Color.parseColor("#000000"))
                binding.tvNotiSettingFirstText.setTextColor(Color.parseColor("#000000"))
                binding.tvNotiTitleFirst.setTextColor(Color.parseColor("#000000"))

                binding.tvNotiSettingInterval.setTextColor(Color.parseColor("#000000"))
                binding.tvNotiSettingIntervalText.setTextColor(Color.parseColor("#000000"))
                binding.tvNotiTitleInterval.setTextColor(Color.parseColor("#000000"))

                binding.tvNotiSettingTime.setTextColor(Color.parseColor("#000000"))
                binding.tvNotiTitleTime.setTextColor(Color.parseColor("#000000"))
            }
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
        //로그아웃
        binding.tvLogout.setOnClickListener {

            val dialogBinding = DialogYcBtnBinding.inflate(LayoutInflater.from(requireContext()))
            val builder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)

            val alertDialog = builder.show()

            // '로그아웃' 버튼 클릭 시 동작
            dialogBinding.btnYes.setOnClickListener {
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

                alertDialog.dismiss()
            }

            // '아니요' 버튼 클릭 시 다이얼로그 닫기
            dialogBinding.btnNo.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        //회원탈퇴
        binding.tvWithdraw.setOnClickListener {

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

    fun showNumberPickerFirstDialog() {
        val dialogBinding = DialogNumpickBinding.inflate(LayoutInflater.from(requireContext()))

        // 넘버픽커 설정
        dialogBinding.npSelect.minValue = 1
        dialogBinding.npSelect.maxValue = 30
        dialogBinding.npSelect.wrapSelectorWheel = false

        // 타이틀과 단위 텍스트 설정
        dialogBinding.tvSelectTitle.text = "마감임박 최초 알림"
        dialogBinding.tvSelect.text = "일전"

        // 다이얼로그 생성
        val builder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val alertDialog = builder.show()

        // '완료' 버튼 클릭 시 동작
        dialogBinding.btnComplete.setOnClickListener {
            val selectedDay = dialogBinding.npSelect.value
            setAlarm(selectedDay) // 알림 설정
            alertDialog.dismiss()
        }
    }

    fun setAlarm(daysBefore: Int) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val calendar = Calendar.getInstance().apply {
            // 현재 날짜에서 daysBefore만큼 뺀 날짜로 설정
            add(Calendar.DAY_OF_YEAR, -daysBefore)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }


    fun showNumberPickerAlramDialog() {
        val dialogBinding = DialogNumpickBinding.inflate(LayoutInflater.from(requireContext()))

        // 넘버픽커 설정
        dialogBinding.npSelect.minValue = 1
        dialogBinding.npSelect.maxValue = 30
        dialogBinding.npSelect.wrapSelectorWheel = false

        // 타이틀과 단위 텍스트 설정
        dialogBinding.tvSelectTitle.text = "마감임박 최초 알림"
        dialogBinding.tvSelect.text = "일전"

        // 다이얼로그 생성
        val builder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val alertDialog = builder.show()

        // '완료' 버튼 클릭 시 동작
        dialogBinding.btnComplete.setOnClickListener {
            val selectedDay = dialogBinding.npSelect.value
            binding.tvNotiSettingInterval.text = selectedDay.toString()
            alertDialog.dismiss()
        }
    }

    fun showTimePickerDialog() {
        val dialogBinding = DialogNumpickBinding.inflate(LayoutInflater.from(requireContext()))
        val times = arrayOf("09:00", "13:00", "18:00")  // 시간 목록

        // 타이틀과 단위 텍스트 설정
        dialogBinding.tvSelectTitle.text = "알림 시간 설정"
        dialogBinding.tvSelect.visibility = View.GONE  // 넘버픽커 삭제

        // 다이얼로그 생성
        val builder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val alertDialog = builder.show()

        // '완료' 버튼 클릭 시 동작
        dialogBinding.btnComplete.setOnClickListener {
            alertDialog.dismiss()
        }

        // 아이템 목록 설정
        dialogBinding.npSelect.minValue = 0
        dialogBinding.npSelect.maxValue = times.size - 1
        dialogBinding.npSelect.displayedValues = times
        dialogBinding.npSelect.wrapSelectorWheel = false
        dialogBinding.npSelect.setOnValueChangedListener { _, _, newVal ->
            binding.tvNotiSettingTime.text = times[newVal]
        }
    }


    private fun updateNicknameInView(nickname: String?) {
        if (!nickname.isNullOrBlank()) {
            val welcomeMessage = "$nickname" + "님 환영합니다."
            binding.root.findViewById<TextView>(R.id.tv_title_account).text = welcomeMessage
        }
    }

}
