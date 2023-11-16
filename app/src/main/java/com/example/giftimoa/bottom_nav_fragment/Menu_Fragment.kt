package com.example.giftimoa.bottom_nav_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.giftimoa.Menu_Mygifticon_activity
import com.example.giftimoa.Menu_favorite_activity
import com.example.giftimoa.databinding.FragmentMenuBinding

class Menu_Fragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding.tvLogout.setOnClickListener {

        }
        binding.tvWithdraw.setOnClickListener {

        }

        binding.switchNoti.setOnCheckedChangeListener { _, isChecked ->
            binding.lNotiFirst.isEnabled = isChecked
            binding.lNotiFirst.isClickable = isChecked

            binding.lNotiInterval.isEnabled = isChecked
            binding.lNotiInterval.isClickable = isChecked

            binding.lNotiTime.isEnabled = isChecked
            binding.lNotiTime.isClickable = isChecked

            // isChecked가 true이면 알림을 설정합니다.
            if (isChecked) {
                // 알림 설정 코드를 여기에 작성합니다.
            } else {
                // 알림을 해제하는 코드를 여기에 작성합니다.
            }
        }

    }
}