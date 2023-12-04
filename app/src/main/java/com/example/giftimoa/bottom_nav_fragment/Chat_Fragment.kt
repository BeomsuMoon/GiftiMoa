package com.example.giftimoa.bottom_nav_fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.Chatting_room_activity
import com.example.giftimoa.R
import com.example.giftimoa.ViewModel.ChatViewModel
import com.example.giftimoa.adpater_list.RecyclerViewChattingRoomAdapter
import com.example.giftimoa.databinding.FragmentChatBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class Chat_Fragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var recyclerViewChattingRoomAdapter: RecyclerViewChattingRoomAdapter
    private lateinit var noChatTextView1: TextView
    private lateinit var noChatTextView2: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var activityResult: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SharedPreferences에서 email 값 불러오기
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "")

        getNicknameFromServer(userEmail) // 서버에서 닉네임 가져오기
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 액션바 설정
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.myToolbar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false) // 타이틀 비활성화
                setDisplayShowHomeEnabled(true)
                setLogo(R.drawable.gm_logo_120)
                setDisplayUseLogoEnabled(true)
            }
        }
        val nickname = binding.root.findViewById<TextView>(R.id.user_nickname).text.toString()

        chatViewModel = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)

        // 리사이클러뷰 어댑터 초기화
        recyclerViewChattingRoomAdapter = RecyclerViewChattingRoomAdapter { chatRoom ->
            // 클릭 이벤트를 처리하는 코드를 여기에 작성합니다.
            val intent = Intent(context, Chatting_room_activity::class.java)
            intent.putExtra(Chatting_room_activity.USERNAME, nickname)
            intent.putExtra("nickname", chatRoom.nickname)
            intent.putExtra("chatroom_id", chatRoom.id)
            startActivity(intent)
        }
        recyclerView = binding.root.findViewById<RecyclerView>(R.id.rv_chat).apply {
            adapter = recyclerViewChattingRoomAdapter
        }


        chatViewModel.fetchChatRooms(nickname)
        chatViewModel.chatRooms.observe(viewLifecycleOwner, { chatRooms ->
            Log.d("ChatFragment", "chatRooms data is updated to: $chatRooms")
            if (chatRooms != null) {
                recyclerViewChattingRoomAdapter.submitList(chatRooms)
            } else {

            }
        })
    }

    //서버에서 이메일 체크하여 닉네임 가져오기
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
    //닉네임 표시 수정 -> ex)Moon님의 대화
    private fun updateNicknameInView(nickname: String?) {
        if (!nickname.isNullOrBlank()) {
            val welcomeMessage = "$nickname"
            val formattedNickname = welcomeMessage.replace("\"", "")
            binding.root.findViewById<TextView>(R.id.user_nickname).text = formattedNickname
        }
    }
}



