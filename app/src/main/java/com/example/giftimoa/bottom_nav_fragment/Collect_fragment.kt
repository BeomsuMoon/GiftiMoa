package com.example.giftimoa.bottom_nav_fragment

import android.content.Intent
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
import com.example.giftimoa.Search_gift_activity
import com.example.giftimoa.adpater_list.RecyclerViewSearchGiftAdapter
import com.example.giftimoa.dto.Search_Gift
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Collect_fragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var recyclerViewCollectGiftAdapter: RecyclerViewSearchGiftAdapter? = null
    private var giftList = mutableListOf<Search_Gift>()

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

        // 플로팅 버튼 클릭 시 다음 화면의 액티비티로 이동
        view.findViewById<FloatingActionButton>(R.id.fab_btn).setOnClickListener {
            val intent = Intent(requireContext(), Collect_gift_add_activity::class.java)
            startActivity(intent)
        }

        //리사이클러뷰 아이템 생성 코드
        //recyclerView = view.findViewById(R.id.rv_Gift_Collect)
        //recyclerViewCollectGiftAdapter = RecyclerViewSearchGiftAdapter(requireActivity() as Search_gift_activity, giftList)
        //val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 2)
        //recyclerView!!.layoutManager = layoutManager
        //recyclerView!!.adapter = recyclerViewCollectGiftAdapter

        // 이 부분에서 데이터를 초기화하도록 수정
       // prepareGiftListData()
    }

    //액션바 옵션(검색)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collect_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    //기프티콘 추가에서 입력한 값 받아오기
    private fun prepareGiftListData() {

        recyclerViewCollectGiftAdapter!!.notifyDataSetChanged()

    }
}
