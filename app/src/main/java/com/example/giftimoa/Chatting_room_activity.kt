package com.example.giftimoa

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giftimoa.adpater_list.RecyclerViewChattingMessageAdapter
import com.example.giftimoa.databinding.LayoutChattingRoomBinding
import com.example.giftimoa.dto.ChatItem
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Chatting_room_activity : AppCompatActivity() {

    private lateinit var binding: LayoutChattingRoomBinding
    private lateinit var socketHandler: SocketHandler
    private lateinit var recyclerViewChattingRoomAdapter: RecyclerViewChattingMessageAdapter
    private val chatList = mutableListOf<ChatItem>()
    private var user_nickname = ""
    private var receiver_user_nickname = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutChattingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user_nickname = intent.getStringExtra(USERNAME) ?: ""
        receiver_user_nickname = intent.getStringExtra(RECIVER_USERNAME) ?: ""

        val chatRoomId = intent.getIntExtra("chatroom_id", -1)
        if (chatRoomId != -1) {
            fetchChatMessages(chatRoomId)
        }

        // Toolbar 설정
        val nickname = intent.getStringExtra("nickname")  // 인텐트에서 닉네임 읽기
        val brand = intent.getStringExtra("brand")
        setupActionBarTitle(nickname, brand)

        socketHandler = SocketHandler()
        recyclerViewChattingRoomAdapter = RecyclerViewChattingMessageAdapter()

        binding.rvChattingRoom.apply {
            binding.rvChattingRoom.layoutManager = LinearLayoutManager(this@Chatting_room_activity)
            adapter = recyclerViewChattingRoomAdapter
        }

        binding.sendBtn.setOnClickListener {
            val messageText = binding.messageText.text.toString()
            if (messageText.isNotEmpty()){
                val currentTimestamp = System.currentTimeMillis()

                // 채팅 리스트가 비어있거나, 마지막 메시지가 오늘 보낸 것이 아니라면 날짜 아이템을 추가
                if (chatList.isEmpty() || !isSameDay(chatList.last().timestamp, currentTimestamp)) {
                    val currentDate = Calendar.getInstance().time
                    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA)

                    val dateMessage = ChatItem(
                        nickname = user_nickname,
                        reciver_nickname = nickname,
                        message = dateFormatter.format(currentDate),
                        timestamp = currentTimestamp,
                        isDate = true,
                        chatroom_id = chatRoomId
                    )
                    chatList.add(dateMessage)
                    recyclerViewChattingRoomAdapter.addMessage(dateMessage)
                }

                val chat = ChatItem(
                    nickname = user_nickname,
                    reciver_nickname = nickname,
                    message = messageText,
                    timestamp = currentTimestamp,
                    chatroom_id = chatRoomId

                )
                socketHandler.emitChat(chat)
                binding.messageText.text.clear()
            }
        }
        socketHandler.onNewChatItem.observe(this) { newItem ->
            newItem?.let {
                val nickname = it.nickname ?: user_nickname

                if (it.nickname != null) {
                    val chat = it.copy(isSelf = it.nickname == user_nickname)
                    chatList.add(chat)
                    recyclerViewChattingRoomAdapter.submitData(chatList)
                    binding.rvChattingRoom.scrollToPosition(chatList.size - 1)
                } else {
                    Log.e("ChattingRoom", "nickname이 null입니다. newItem: $newItem")
                    // 사용자에게 에러 메시지를 보여줄 수 있음
                }
            } ?: Log.e("ChattingRoom", "Received newItem is null")
        }



        //키보드 내리기
        binding.rvChattingRoom.setOnTouchListener { _, _ -> //리사이클러뷰 영역 터치 시 키보드 내리기
            hideKeyboard()
            false
        }

    }
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun fetchChatMessages(chatRoomId: Int) {
        val url = "http://3.35.110.246:3306/chatroom/$chatRoomId/messages"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 네트워크 요청 실패 시 처리
                e.printStackTrace()
                Log.e("ChattingRoom", "채팅 메시지 가져오기 실패", e)
                runOnUiThread {
                    // 사용자에게 에러 메시지를 보여주는 작업 수행 (Toast 등)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("ChattingRoom", "서버 응답 실패. 코드: ${response.code}")
                    runOnUiThread {
                        // 사용자에게 에러 메시지를 보여주는 작업 수행 (Toast 등)
                    }
                    return
                }

                // 네트워크 요청 성공 시 처리
                val responseBody = response.body?.string()
                if (responseBody == null) {
                    Log.e("ChattingRoom", "응답 본문이 비어 있음")
                    runOnUiThread {
                        // 사용자에게 에러 메시지를 보여주는 작업 수행 (Toast 등)
                    }
                    return
                }

                val messages: Array<ChatItem>
                try {
                    messages = Gson().fromJson(responseBody, Array<ChatItem>::class.java)
                } catch (e: JsonSyntaxException) {
                    Log.e("ChattingRoom", "채팅 메시지 파싱 실패", e)
                    runOnUiThread {
                        // 사용자에게 에러 메시지를 보여주는 작업 수행 (Toast 등)
                    }
                    return
                }
                runOnUiThread {
                    chatList.addAll(messages)
                    recyclerViewChattingRoomAdapter.submitData(chatList)
                    Log.d("ChattingRoom", "채팅 메시지 성공적으로 가져옴")
                }
            }
        })
    }

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

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

        val date1 = Date(timestamp1)
        val date2 = Date(timestamp2)

        return dateFormat.format(date1) == dateFormat.format(date2)
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        socketHandler.disconnectSocket()
        super.onDestroy()
    }

    companion object{
        const val USERNAME = "username"
        const val RECIVER_USERNAME = "reciver_username"
    }
}