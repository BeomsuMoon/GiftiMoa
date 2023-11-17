package com.example.giftimoa.bottom_nav_fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.Collect_gift_add_activity
import com.example.giftimoa.Collect_gift_add_info_activity
import com.example.giftimoa.R
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.adpater_list.RecyclerViewCollectGiftAdapter
import com.example.giftimoa.dto.Collect_Gift
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Collect_fragment : Fragment() {
    private lateinit var giftViewModel: Gificon_ViewModel
    private lateinit var recyclerViewCollectGiftAdapter: RecyclerViewCollectGiftAdapter
    private lateinit var noGifticonTextView: TextView
    private lateinit var recyclerView: RecyclerView


    private lateinit var activityResult: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //새로운 액티비티를 시작하고 그 결과를 받기 위한 콜백을 등록하는 메서드
        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val collectGift = result.data!!.extras!!.get("gift") as Collect_Gift
                giftViewModel.addGift(collectGift)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // 프래그먼트에서 옵션 메뉴 사용을 활성화
        giftViewModel = ViewModelProvider(requireActivity()).get(Gificon_ViewModel::class.java)
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

    private fun startCollectGiftAddActivity() {
        val intent = Intent(requireContext(), Collect_gift_add_activity::class.java)
        activityResult.launch(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noGifticonTextView = view.findViewById<TextView>(R.id.tv_noGifticon)
        recyclerView = view.findViewById<RecyclerView>(R.id.rv_Gift_Collect)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView.layoutManager = layoutManager

        // 어댑터 초기화 및 RecyclerView에 연결
        recyclerViewCollectGiftAdapter = RecyclerViewCollectGiftAdapter(mutableListOf()) { gift ->
            // Start the new activity
            val intent = Intent(requireContext(), Collect_gift_add_info_activity::class.java)
            intent.putExtra("gift", gift)
            startActivity(intent)
        }
        recyclerView.adapter = recyclerViewCollectGiftAdapter

        // 플로팅 버튼 클릭 시 다음 화면의 액티비티로 이동(기프티콘 등록)
        view.findViewById<FloatingActionButton>(R.id.fab_btn).setOnClickListener {
            startCollectGiftAddActivity()
        }

        giftViewModel.collectGifts.observe(viewLifecycleOwner, { gifts ->
            // 어댑터에 데이터 업데이트
            recyclerViewCollectGiftAdapter.setGiftList(gifts.toMutableList())
            recyclerViewCollectGiftAdapter.notifyDataSetChanged()
            Log.d("로그", "기프티콘: $gifts")

            // 만약 기프티콘이 등록 되면 등록안내 문구 hide 아니면 show
            if (gifts.isEmpty()) {
                noGifticonTextView.visibility = View.VISIBLE
            } else {
                noGifticonTextView.visibility = View.GONE
            }
        })
    }

    //액션바 옵션(검색)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collect_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}


