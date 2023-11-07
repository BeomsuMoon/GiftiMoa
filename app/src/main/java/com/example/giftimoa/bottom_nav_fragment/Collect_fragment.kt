package com.example.giftimoa.bottom_nav_fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.Collect_gift_add_activity
import com.example.giftimoa.Collect_gift_add_info_activity
import com.example.giftimoa.R
import com.example.giftimoa.ViewModel.Gifticon_ViewModel
import com.example.giftimoa.adpater_list.RecyclerViewCollectGiftAdapter
import com.example.giftimoa.adpater_list.RecyclerViewSearchGiftAdapter
import com.example.giftimoa.databinding.FragmentCollectBinding
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.home_fragment_List.Search_gift_activity
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class Collect_fragment : Fragment() {

    private lateinit var binding : FragmentCollectBinding
    private var recyclerView: RecyclerView? = null
    private var recyclerViewCollectGiftAdapter: RecyclerViewCollectGiftAdapter? = null
    private var giftList = mutableListOf<Collect_Gift>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // 프래그먼트에서 옵션 메뉴 사용을 활성화
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collect, container, false)

        //툴바 활성화
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "GIFTIMOA"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_Gift_Collect)
        recyclerViewCollectGiftAdapter = RecyclerViewCollectGiftAdapter(giftList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = recyclerViewCollectGiftAdapter

        addGift()


        // 플로팅 버튼 클릭 시 다음 화면의 액티비티로 이동
        view.findViewById<FloatingActionButton>(R.id.fab_btn).setOnClickListener {
            val intent = Intent(requireContext(), Collect_gift_add_activity::class.java)
            startActivity(intent)

        }
    }
    private fun addGift() {
        // 데이터 생성 예시 (실제로 사용자 입력 등의 방식으로 데이터를 생성)

        val giftName = arguments?.getString("giftName")
        val effectiveDate = arguments?.getString("effectiveDate")
        val barcode = arguments?.getString("barcode")
        val usage = arguments?.getString("usage")


        // Collect_Gift 객체 생성
        if (giftName != null && effectiveDate != null && barcode != null && usage != null) {
            val gift = Collect_Gift(giftName, effectiveDate, barcode, usage)
            giftList.add(gift)
            recyclerViewCollectGiftAdapter?.notifyDataSetChanged()
        } else {
            Log.d(TAG,"데이터 삽입 오류")
        }

    }


    //액션바 옵션(검색)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collect_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}