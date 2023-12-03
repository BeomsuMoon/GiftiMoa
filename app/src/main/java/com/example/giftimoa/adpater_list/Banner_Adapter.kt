package com.example.giftimoa.adpater_list

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.giftimoa.Banner_Fragment.Banner_Fragment_1
import com.example.giftimoa.Banner_Fragment.Banner_Fragment_2
import com.example.giftimoa.Banner_Fragment.Banner_activity_1
import com.example.giftimoa.Banner_Fragment.Banner_activity_2

class Banner_Adapter(private val fa: FragmentActivity, private val count: Int) : FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {
        val index = getRealPosition(position)
        val fragment = when (index) {
            0 -> Banner_Fragment_1()
            else -> Banner_Fragment_2()
        }

        // 프래그먼트의 뷰가 생성된 후에 클릭 리스너를 설정해야 합니다.
        fragment.viewLifecycleOwnerLiveData.observe(fa, { viewLifecycleOwner ->
            if (viewLifecycleOwner != null) {
                // 뷰가 생성된 경우 클릭 리스너를 설정합니다.
                setOnClickListener(fragment.requireView(), index)
            }
        })
        return fragment
    }

    override fun getItemCount(): Int {
        return 2000
    }

    private fun getRealPosition(position: Int): Int {
        return position % count
    }

    private fun setOnClickListener(view: View, index: Int) {
        view.setOnClickListener {
            val intent = when (index) {
                0 -> Intent(fa, Banner_activity_1::class.java)
                else -> Intent(fa, Banner_activity_2::class.java)
            }
            fa.startActivity(intent)
        }
    }
}