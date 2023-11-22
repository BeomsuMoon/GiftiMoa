package com.example.giftimoa.bottom_nav_fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.giftimoa.AlarmReceiver
import com.example.giftimoa.Login_activity
import com.example.giftimoa.Menu_Mygifticon_activity
import com.example.giftimoa.Menu_favorite_activity
import com.example.giftimoa.Menu_profile_edit
import com.example.giftimoa.R
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.DialogNumpickBinding
import com.example.giftimoa.databinding.DialogYcBtnBinding
import com.example.giftimoa.databinding.FragmentMenuBinding
import com.example.giftimoa.dto.Collect_Gift
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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


        //나중에 DB로 대체 되어야 함
        val sharedPreferencesFirst = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val firstNoti = sharedPreferencesFirst.getInt("first_noti", 0)
        val intervalNoti = sharedPreferencesFirst.getInt("interval_noti", 0)
        val timeNoti = sharedPreferencesFirst.getString("time_noti", "")

        // 불러온 알림 설정 정보를 뷰에 설정합니다.
        binding.tvNotiSettingFirst.text = firstNoti.toString()
        binding.tvNotiSettingInterval.text = intervalNoti.toString()
        binding.tvNotiSettingTime.text = timeNoti.toString()
        // 초기 상태 설정 (스위치를 활성 상태로 설정)
        initializeViewState()
        // 스위치의 체크 상태 변경 리스너 설정
        binding.switchNoti.setOnCheckedChangeListener { _, isChecked ->
            binding.lNotiFirst.isClickable = isChecked
            binding.lNotiInterval.isClickable = isChecked
            binding.lNotiTime.isClickable = isChecked

            if (!isChecked) {
                scheduleAlarms()
            } else {
                cancelAlarms()
            }

            setTextColor(isChecked)
        }

        //프로필수정 빈공간 눌렀을떄
        binding.lAccount.setOnClickListener {
            val intent = Intent(requireContext(), Menu_profile_edit::class.java)

            // TextView에서 닉네임 가져오기
            val fullNickname = binding.root.findViewById<TextView>(R.id.tv_title_account).text.toString()

            // '님 환영합니다' 부분 제거
            val nicknameWithoutGreeting = fullNickname.replace("님 환영합니다", "").trim()

            // 수정된 닉네임이 비어있지 않은 경우에만 인텐트에 추가
            if (nicknameWithoutGreeting.isNotEmpty()) {
                // 인텐트에 수정된 닉네임 추가
                intent.putExtra("nickname", nicknameWithoutGreeting)

                // 액티비티 시작
                startActivity(intent)
            } else {
                Log.d("test","$nicknameWithoutGreeting , $fullNickname")
            }
        }

        //프로필수정 버튼 눌렀을때
        binding.profileEditBtn.setOnClickListener {
            val intent = Intent(requireContext(), Menu_profile_edit::class.java)

            // TextView에서 닉네임 가져오기
            val fullNickname = binding.root.findViewById<TextView>(R.id.tv_title_account).text.toString()

            // '님 환영합니다' 부분 제거
            val nicknameWithoutGreeting = fullNickname.replace("님 환영합니다", "").trim()

            // 수정된 닉네임이 비어있지 않은 경우에만 인텐트에 추가
            if (nicknameWithoutGreeting.isNotEmpty()) {
                // 인텐트에 수정된 닉네임 추가
                intent.putExtra("nickname", nicknameWithoutGreeting)

                // 액티비티 시작
                startActivity(intent)
            } else {
                Log.d("test","$nicknameWithoutGreeting , $fullNickname")
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

    //기프티콘 알림 활성화
    fun scheduleAlarms() {
        // ViewModel을 통해 기프티콘 리스트를 가져옵니다.
        val viewModel = ViewModelProvider(requireActivity()).get(Gificon_ViewModel::class.java)
        val gifticonList = viewModel.collectGifts.value

        // 기프티콘 리스트가 비어있지 않을 때만 알림을 설정합니다.
        if (!gifticonList.isNullOrEmpty()) {
            // SharedPreferences에서 알림 설정 정보를 불러옵니다.
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val firstNoti = sharedPreferences.getInt("first_noti", 0)
            val intervalNoti = sharedPreferences.getInt("interval_noti", 0)
            val timeNoti = sharedPreferences.getString("time_noti", "")

            for (gifticon in gifticonList) {
                // 최초 알림 설정
                setAlarm(firstNoti, gifticon)
                // 주기적 알림 설정
                setRepeatedAlarm(intervalNoti, timeNoti, gifticon)
            }
        }
    }
    
    
    //기프티콘 알림 비활성화
    fun cancelAlarms() {
        // ViewModel을 통해 기프티콘 리스트를 가져옵니다.
        val viewModel = ViewModelProvider(requireActivity()).get(Gificon_ViewModel::class.java)
        val gifticonList = viewModel.collectGifts.value

        // 기프티콘 리스트가 비어있지 않을 때만 알람을 취소합니다.
        if (!gifticonList.isNullOrEmpty()) {
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            for (gifticon in gifticonList) {
                val intent = Intent(context, AlarmReceiver::class.java)
                // 각 기프티콘에 대한 고유한 PendingIntent를 생성합니다.
                val pendingIntent = PendingIntent.getBroadcast(context, gifticon.ID, intent, PendingIntent.FLAG_IMMUTABLE or 0)
                // 알람 취소
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    //기프티콘 주기적 알림 설정
    fun setRepeatedAlarm(interval: Int, time: String?, gifticon: Collect_Gift) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("giftName", gifticon.giftName)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or 0)

        // String 형식의 effectiveDate를 Date 형식으로 변환합니다.
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val gifticonEffectiveDate: Date = sdf.parse(gifticon.effectiveDate)

        // SharedPreferences에서 알림 시간과 주기를 불러옵니다.
        val alarmHour = time?.split(":")?.get(0)?.toInt() ?: 0
        val alarmMinute = time?.split(":")?.get(1)?.toInt() ?: 0

        // 사용자가 설정한 알림을 받고자 하는 날짜와 시간을 계산합니다.
        val calendar = Calendar.getInstance().apply {
            val format = SimpleDateFormat("HH:mm")
            val newTime = format.format(gifticonEffectiveDate)
            add(Calendar.DAY_OF_YEAR, -interval)
            set(Calendar.HOUR_OF_DAY, alarmHour)
            set(Calendar.MINUTE, alarmMinute)
            set(Calendar.SECOND, 0)
        }
        // 알림 주기를 밀리세컨드로 변환
        val intervalMillis = interval * 24 * 60 * 60 * 1000L
        // 알람 설정
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, intervalMillis, pendingIntent)
    }


    //등록한 기프티콘의 유효기간 만료 안내 알림
    fun setAlarm(daysBefore: Int, gifticon: Collect_Gift) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("giftName", gifticon.giftName)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or 0)

        // String 형식의 effectiveDate를 Date 형식으로 변환합니다.
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val gifticonEffectiveDate: Date = sdf.parse(gifticon.effectiveDate)

        // SharedPreferences에서 알림 시간과 주기를 불러옵니다.
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val alarmTime = sharedPreferences.getString("time_noti", "09:00") ?: "09:00"
        val alarmInterval = sharedPreferences.getInt("interval_noti", 1)
        val alarmHour = alarmTime.split(":")[0].toInt()
        val alarmMinute = alarmTime.split(":")[1].toInt()

        // 사용자가 설정한 알림을 받고자 하는 날짜와 시간을 계산합니다.
        val calendar = Calendar.getInstance().apply {
            time = gifticonEffectiveDate
            add(Calendar.DAY_OF_YEAR, -daysBefore)
            set(Calendar.HOUR_OF_DAY, alarmHour)
            set(Calendar.MINUTE, alarmMinute)
            set(Calendar.SECOND, 0)
        }

        // 알림 주기를 밀리세컨드로 변환
        val intervalMillis = alarmInterval * 24 * 60 * 60 * 1000L

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 레벨 23 이상에서는 setExactAndAllowWhileIdle() 함수를 사용
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // API 레벨 19 이상에서는 setExact() 함수를 사용
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            // API 레벨 19 미만에서는 set() 함수를 사용
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }


    //마감임박 최초 알림 다이얼로그
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

            // ViewModel을 통해 기프티콘 리스트를 가져옵니다.
            val viewModel = ViewModelProvider(requireActivity()).get(Gificon_ViewModel::class.java)
            val gifticonList = viewModel.collectGifts.value

            // 기프티콘 리스트가 비어있지 않을 때만 알림을 설정합니다.
            if (!gifticonList.isNullOrEmpty()) {
                for (gifticon in gifticonList) {
                    setAlarm(selectedDay, gifticon)
                }
            }

            // SharedPreferences에 알림 설정 정보를 저장합니다. 나중에 DB로 대체
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("first_noti", selectedDay)
            editor.apply()

            binding.tvNotiSettingFirst.text = selectedDay.toString()
            alertDialog.dismiss()
        }

    }

    //마감 주기 알림 다이얼로그
    fun showNotificationIntervalPickerDialog() {
        val dialogBinding = DialogNumpickBinding.inflate(LayoutInflater.from(requireContext()))

        // 넘버픽커 설정
        dialogBinding.npSelect.minValue = 1
        dialogBinding.npSelect.maxValue = 10
        dialogBinding.npSelect.wrapSelectorWheel = false

        // 타이틀과 단위 텍스트 설정
        dialogBinding.tvSelectTitle.text = "알림 주기 설정"
        dialogBinding.tvSelect.text = "일마다"

        // 다이얼로그 생성
        val builder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val alertDialog = builder.show()

        // '완료' 버튼 클릭 시 동작
        dialogBinding.btnComplete.setOnClickListener {
            val selectedInterval = dialogBinding.npSelect.value
            binding.tvNotiSettingInterval.text = selectedInterval.toString()
            alertDialog.dismiss()

            // 알람 주기 설정
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
            // 주기를 밀리세컨드로 변환
            val intervalMillis = selectedInterval * 24 * 60 * 60 * 1000L

            // 알람 설정
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                intervalMillis, pendingIntent)

            // SharedPreferences에 알림 주기 설정값 저장
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("interval_noti", selectedInterval)
            editor.apply()

            // 선택된 알림 주기를 로그로 출력
            Log.d("NotificationInterval", "Selected interval: $selectedInterval")
        }
    }
    
    //알림 받는 시간 다이얼로그
    fun showTimePickerDialog() {
        val dialogBinding = DialogNumpickBinding.inflate(LayoutInflater.from(requireContext()))
        val times = arrayOf("09:00", "13:00", "18:00")  // 시간 목록

        // 타이틀과 단위 텍스트 설정
        dialogBinding.tvSelectTitle.text = "알림 시간 설정"
        dialogBinding.tvSelect.visibility = View.GONE  // 넘버픽커 삭제

        // 다이얼로그 생성
        val builder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val alertDialog = builder.show()

        // 아이템 목록 설정
        dialogBinding.npSelect.minValue = 0
        dialogBinding.npSelect.maxValue = times.size - 1
        dialogBinding.npSelect.displayedValues = times
        dialogBinding.npSelect.wrapSelectorWheel = false

        // '완료' 버튼 클릭 시 동작
        dialogBinding.btnComplete.setOnClickListener {
            val selectedTime = times[dialogBinding.npSelect.value]
            binding.tvNotiSettingTime.text = selectedTime

            // SharedPreferences에 알림 시간 설정값 저장
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("time_noti", selectedTime)
            editor.apply()

            // 다이얼로그 창 닫기
            alertDialog.dismiss()
        }
    }


    fun setTextColor(isChecked: Boolean) {
        val color = if (isChecked) "#000000" else "#939393"

        binding.tvNotiSettingFirst.setTextColor(Color.parseColor(color))
        binding.tvNotiSettingFirstText.setTextColor(Color.parseColor(color))
        binding.tvNotiTitleFirst.setTextColor(Color.parseColor(color))
        binding.tvNotiSettingInterval.setTextColor(Color.parseColor(color))
        binding.tvNotiSettingIntervalText.setTextColor(Color.parseColor(color))
        binding.tvNotiTitleInterval.setTextColor(Color.parseColor(color))
        binding.tvNotiSettingTime.setTextColor(Color.parseColor(color))
        binding.tvNotiTitleTime.setTextColor(Color.parseColor(color))
    }
    fun initializeViewState() {
        binding.switchNoti.isChecked = true
        binding.lNotiFirst.isClickable = true
        binding.lNotiInterval.isClickable = true
        binding.lNotiTime.isClickable = true
        binding.lNotiFirst.setOnClickListener { showNumberPickerFirstDialog() }
        binding.lNotiInterval.setOnClickListener { showNotificationIntervalPickerDialog() }
        binding.lNotiTime.setOnClickListener { showTimePickerDialog() }

        setTextColor(true)  // 위에서 정의한 색깔 변경 함수 호출
    }


    private fun updateNicknameInView(nickname: String?) {
        if (!nickname.isNullOrBlank()) {
            val welcomeMessage = "$nickname" + "님 환영합니다."
            // 큰 따옴표를 제거합니다.
            val formattedNickname = welcomeMessage.replace("\"", "")
            binding.root.findViewById<TextView>(R.id.tv_title_account).text = formattedNickname
        }
    }

}
