package com.example.giftimoa.bottom_nav_fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.Collect_gift_add_activity
import com.example.giftimoa.R
import com.example.giftimoa.adpater_list.RecyclerViewCollectGiftAdapter
import com.example.giftimoa.databinding.FragmentCollectBinding
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.home_fragment_List.Search_gift_activity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Collect_fragment : Fragment() {

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
        val giftName = arguments?.getString("giftName")
        val effectiveDate = arguments?.getString("effectiveDate")
        val barcode = arguments?.getString("barcode")
        val usage = arguments?.getString("usage")

        val imageUriString = arguments?.getString("imageUri")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        recyclerView = view.findViewById(R.id.rv_Gift_Collect)
        recyclerViewCollectGiftAdapter = RecyclerViewCollectGiftAdapter(giftList) // giftList should contain your data
        recyclerView?.adapter = recyclerViewCollectGiftAdapter
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)


        // 플로팅 버튼 클릭 시 다음 화면의 액티비티로 이동
        view.findViewById<FloatingActionButton>(R.id.fab_btn).setOnClickListener {
            val intent = Intent(requireContext(), Collect_gift_add_activity::class.java)
            startActivity(intent)

        }
    }

    private fun addGift(){

    }
    //액션바 옵션(검색)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collect_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}