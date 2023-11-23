package com.example.giftimoa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giftimoa.adpater_list.RecyclerViewChattingGiftAdapter
import com.example.giftimoa.databinding.LayoutChattingRoomBinding
import com.example.giftimoa.dto.ChatItem

class Chatting_room_activity : AppCompatActivity() {

    private lateinit var binding: LayoutChattingRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 사용
        binding = LayoutChattingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // RecyclerView 초기화
        binding.rvChattingRoom.layoutManager = LinearLayoutManager(this)

// 어댑터 초기화
        val adapter = RecyclerViewChattingGiftAdapter()
        binding.rvChattingRoom.adapter = adapter

// 메시지 추가
        val chatMessage = ChatItem(message = "Hello", timestamp = System.currentTimeMillis())
        adapter.addMessage(chatMessage)



    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}
