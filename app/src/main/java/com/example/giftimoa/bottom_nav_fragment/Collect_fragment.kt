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

class Collect_fragment : Fragment() {

    private var giftName: String = ""
    private var effectiveDate: String = ""
    private var barcode: String = ""
    private var usage: String = ""

    private lateinit var binding : FragmentCollectBinding

    private val viewModel: Gifticon_ViewModel by lazy {
        ViewModelProvider(this).get(Gifticon_ViewModel::class.java)
    }

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

    private fun addGift(){
// 데이터 설정
        viewModel.setGiftData("선물 이름", "2023-11-07", "바코드 값", "브랜드 이름")
// 데이터 가져오기
        val giftData = viewModel.getGiftData()
    }


    //액션바 옵션(검색)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collect_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}