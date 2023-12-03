package com.example.giftimoa.bottom_nav_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.giftimoa.R
import com.example.giftimoa.databinding.FragmentChatBinding

class Chat_Fragment : Fragment() {
    private lateinit var binding: FragmentChatBinding

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
    }
}



