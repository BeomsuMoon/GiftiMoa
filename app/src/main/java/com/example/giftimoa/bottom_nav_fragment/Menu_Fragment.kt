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

        binding.tvFavorite.setOnClickListener {
            val intent = Intent(requireContext(), Menu_favorite_activity::class.java)
            startActivity(intent)
        }
        binding.myGifticon.setOnClickListener {
            val intent = Intent(requireContext(), Menu_Mygifticon_activity::class.java)
            startActivity(intent)
        }
        binding.tvLogout.setOnClickListener {

        }
        binding.tvWithdraw.setOnClickListener {

        }
        // 나머지 뷰에 대해서도 동일하게 처리하면 됩니다.
        binding.switchNoti.setOnCheckedChangeListener { _, isChecked ->
            // 체크 상태 변경 시 수행할 작업을 여기에 작성합니다.
        }
    }
}