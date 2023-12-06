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
    private lateinit var noChatTextView1: TextView
    private lateinit var noChatTextView2: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatViewModel: ChatViewModel
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
        val nickname = binding.root.findViewById<TextView>(R.id.user_nickname).text.toString()
        // 리사이클러뷰 어댑터 초기화
        recyclerViewChattingRoomAdapter = RecyclerViewChattingRoomAdapter { chatItem ->
            val bundle = Bundle()
            bundle.putString("renickname", chatItem.nickname)
            bundle.putString("brand", chatItem.brand)
            bundle.putString("myNickname", nickname) // 내 닉네임 추가
            val dialogFragment = ChatDialogFragment.newInstance(bundle)
            dialogFragment.show(parentFragmentManager, "ChatDialogFragment")
        }

        recyclerView = binding.root.findViewById<RecyclerView>(R.id.rv_chat).apply {
            adapter = recyclerViewChattingRoomAdapter
        }

        lifecycleScope.launch {

            chatViewModel.fetchChatMessages(nickname)
            chatViewModel.chatMessages.observe(viewLifecycleOwner, { chatMessages ->
                Log.d("ChatFragment", "chatMessages data is updated to: $chatMessages")
                if (chatMessages != null) {
                    recyclerViewChattingRoomAdapter.submitList(chatMessages)
                }
            })
        }
    }

    private fun getNicknameFromServer(userEmail: String?) {
        lifecycleScope.launch {
            val client = OkHttpClient()
            val url = "http://3.35.110.246:3306/getNicknameByEmail"

            if (!userEmail.isNullOrBlank()) {
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
                    activity?.runOnUiThread {
                        updateNicknameInView(responseData)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("Chat_Fragment", "nickname_get error")
                }
            }
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
