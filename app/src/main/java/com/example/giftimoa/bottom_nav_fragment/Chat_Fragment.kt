package com.example.giftimoa.bottom_nav_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.ChatDialogFragment
import com.example.giftimoa.R
import com.example.giftimoa.ViewModel.ChatViewModel
import com.example.giftimoa.adpater_list.RecyclerViewChattingRoomAdapter
import com.example.giftimoa.databinding.FragmentChatBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class Chat_Fragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var recyclerViewChattingRoomAdapter: RecyclerViewChattingRoomAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var noChatTextView1: TextView
    private lateinit var noChatTextView2: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        noChatTextView1 = binding.root.findViewById(R.id.noChatTextView1)
        noChatTextView2 = binding.root.findViewById(R.id.noChatTextView2)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)

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

        binding
        lifecycleScope.launch {
            // getNicknameFromServer 함수 호출
            val sharedPreferences =
                requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("user_email", "")
            val nickname = getNicknameFromServer(userEmail)

            // 이후에 닉네임을 사용
            chatViewModel.fetchChatMessages(nickname)
            chatViewModel.chatMessages.observe(viewLifecycleOwner, { chatMessages ->
                Log.d("ChatFragment", "chatMessages data is updated to: $chatMessages")
                if (chatMessages != null) {
                    recyclerViewChattingRoomAdapter.submitList(chatMessages)
                }
                if (chatMessages.isNullOrEmpty()) {
                    noChatTextView1.visibility = View.VISIBLE
                    noChatTextView2.visibility = View.VISIBLE
                } else {
                    noChatTextView1.visibility = View.GONE
                    noChatTextView2.visibility = View.GONE
                }
            })
        }

        // 리사이클러뷰 어댑터 초기화
        recyclerViewChattingRoomAdapter = RecyclerViewChattingRoomAdapter { chatItem ->
            val bundle = Bundle()
            bundle.putString("renickname", chatItem.nickname)
            bundle.putString("brand", chatItem.brand)
            bundle.putString("myNickname", binding.userNickname.text.toString()) // 내 닉네임 추가
            val dialogFragment = ChatDialogFragment.newInstance(bundle)
            dialogFragment.show(parentFragmentManager, "ChatDialogFragment")
        }

        recyclerView = binding.root.findViewById(R.id.rv_chat)
        recyclerView.adapter = recyclerViewChattingRoomAdapter
    }

    private suspend fun getNicknameFromServer(userEmail: String?): String {
        val client = OkHttpClient()
        val url = "http://3.35.110.246:3306/getNicknameByEmail"


        return if (!userEmail.isNullOrBlank()) {
            val json = """{"email": "$userEmail"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }
                val responseData = response.body?.string()
                Log.d("Chat_Fragment", "Received nickname from server: $responseData")
                activity?.runOnUiThread {
                    updateNicknameInView(responseData)
                }
                responseData.orEmpty() // 닉네임을 반환 (null일 경우 빈 문자열 반환)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Chat_Fragment", "nickname_get error")
                "" // 에러가 발생할 경우 빈 문자열 반환 또는 적절한 오류 처리
            }
        } else {
            ""
        }
    }

    // 닉네임 표시 수정 -> ex)Moon님의 대화
    private fun updateNicknameInView(nickname: String?) {
        if (!nickname.isNullOrBlank()) {
            val welcomeMessage = "$nickname"
            val formattedNickname = welcomeMessage.replace("\"", "")
            binding.root.findViewById<TextView>(R.id.user_nickname).text = formattedNickname
        }
    }
}
