package com.example.giftimoa

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giftimoa.adpater_list.RecyclerViewChattingRoomAdapter
import com.example.giftimoa.databinding.LayoutChattingRoomBinding
import com.example.giftimoa.dto.ChatItem
import com.google.android.material.internal.ViewUtils.hideKeyboard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Chatting_room_activity : AppCompatActivity() {

    private lateinit var binding: LayoutChattingRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 사용
        binding = LayoutChattingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        val nickname = intent.getStringExtra("nickname")  // 인텐트에서 닉네임 읽기
        val brand = intent.getStringExtra("brand")
        setupActionBarTitle(nickname, brand)
        
        // RecyclerView 초기화
        binding.rvChattingRoom.layoutManager = LinearLayoutManager(this)
        binding.rvChattingRoom.setOnTouchListener { _, _ -> //리사이클러뷰 영역 터치 시 키보드 내리기
            hideKeyboard()
            false
        }
        // 어댑터 초기화
        val adapter = RecyclerViewChattingRoomAdapter()
        binding.rvChattingRoom.adapter = adapter

        // 보내기 버튼 클릭 이벤트
        binding.button.setOnClickListener {
            val messageText = binding.messageText.text.toString()

            // 메시지가 비어있지 않은 경우에만 메시지를 추가
            if (messageText.isNotEmpty()) {
                // 채팅 메시지가 처음으로 추가되는 경우 현재 날짜를 추가
                if (adapter.itemCount == 0) {
                    val currentDate = Calendar.getInstance().time
                    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA)
                    val dateMessage = ChatItem(message = dateFormatter.format(currentDate), timestamp = System.currentTimeMillis(), isDate = true)
                    adapter.addMessage(dateMessage)
                }

                val chatMessage = ChatItem(message = messageText, timestamp = System.currentTimeMillis())
                adapter.addMessage(chatMessage)

                // 입력 필드 초기화
                binding.messageText.text.clear()
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    //타이틀바 설정
    private fun setupActionBarTitle(nickname: String?, brand: String?) {
        // 주 제목과 서브 제목을 포함하는 레이아웃 생성
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // 주 제목 설정
        val titleText = TextView(this)
        titleText.text = nickname + "님과의 채팅"
        titleText.textSize = 18f
        titleText.setTextColor(Color.BLACK)
        layout.addView(titleText)  // 레이아웃에 추가

        // 서브 제목 설정
        val subtitleText = TextView(this)
        subtitleText.text = "$brand 기프티콘"
        subtitleText.textSize = 14f
        subtitleText.setTextColor(Color.BLACK)
        layout.addView(subtitleText)  // 레이아웃에 추가

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = layout  // 레이아웃을 액션바의 customView로 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}