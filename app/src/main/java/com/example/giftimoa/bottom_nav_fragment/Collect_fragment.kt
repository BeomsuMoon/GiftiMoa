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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.Collect_gift_add_activity
import com.example.giftimoa.Collect_gift_add_info_activity
import com.example.giftimoa.R
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.adpater_list.RecyclerViewCollectGiftAdapter
import com.example.giftimoa.dto.Collect_Gift
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class Collect_fragment : Fragment() {

    private lateinit var giftViewModel: Gificon_ViewModel
    private lateinit var recyclerViewCollectGiftAdapter: RecyclerViewCollectGiftAdapter
    private lateinit var noGifticonTextView1: TextView
    private lateinit var noGifticonTextView2: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var activityResult: ActivityResultLauncher<Intent>

    // 활동과 연결되면서 ActivityResult를 처리하는 런처
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // ActivityResult를 처리하기 위한 런처 등록
        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                // 결과에서 gift를 가져와서 ViewModel에 추가하고 RecyclerView에 추가
                val collectGift = result.data!!.extras!!.get("gift") as Collect_Gift
                giftViewModel.addGift(collectGift)
                recyclerViewCollectGiftAdapter.addGift(collectGift) // 아이템을 목록의 맨 앞에 추가
            }
        }
    }

    // Fragment가 활성화될 때 호출되는 메서드
    override fun onResume() {
        super.onResume()

        // 코루틴을 사용하여 fetchGiftListFromRepository 호출
        viewLifecycleOwner.lifecycleScope.launch {
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("user_email", null)
            if (userEmail != null && userEmail.isNotEmpty()) {
                // 사용자 이메일이 있으면 Repository에서 선물 목록을 가져옴
                giftViewModel.fetchGiftListFromRepository(requireContext(), userEmail)
            } else {
                Log.d("tests", "tests : $userEmail")
            }
        }
    }

    // Fragment가 생성될 때 호출되는 메서드
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // ViewModel 초기화
        giftViewModel = ViewModelProvider(requireActivity()).get(Gificon_ViewModel::class.java)
    }

    // Fragment가 뷰를 생성할 때 호출되는 메서드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collect, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setLogo(R.drawable.gm_logo_120)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)
        return view
    }

    // 새로운 선물을 추가하는 화면으로 이동하는 메서드
    private fun startCollectGiftAddActivity() {
        val intent = Intent(requireContext(), Collect_gift_add_activity::class.java)
        activityResult.launch(intent)
    }

    // 뷰가 생성된 후 호출되는 메서드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noGifticonTextView1 = view.findViewById<TextView>(R.id.tv_noGifticon1)
        noGifticonTextView2 = view.findViewById<TextView>(R.id.tv_noGifticon2)
        recyclerView = view.findViewById<RecyclerView>(R.id.rv_Gift_Collect)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView.layoutManager = layoutManager

        // RecyclerView 어댑터 초기화
        recyclerViewCollectGiftAdapter = RecyclerViewCollectGiftAdapter(mutableListOf()) { gift ->
            // 아이템 클릭 시 상세 정보 화면으로 이동
            val intent = Intent(requireContext(), Collect_gift_add_info_activity::class.java)
            intent.putExtra("gift", gift)
            startActivity(intent)
        }

        recyclerView.adapter = recyclerViewCollectGiftAdapter

        // FloatingActionButton 클릭 시 새로운 선물 추가 화면으로 이동
        view.findViewById<FloatingActionButton>(R.id.fab_btn).setOnClickListener {
            startCollectGiftAddActivity()
        }

        // 코루틴을 사용하여 fetchGiftListFromRepository 호출
        viewLifecycleOwner.lifecycleScope.launch {
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("user_email", null)
            if (userEmail != null && userEmail.isNotEmpty()) {
                // 사용자 이메일이 있으면 Repository에서 선물 목록을 가져옴
                giftViewModel.fetchGiftListFromRepository(requireContext(), userEmail)
            } else {
                Log.d("test", "test : $userEmail")
                noGifticonTextView1.visibility = View.VISIBLE
                noGifticonTextView2.visibility = View.VISIBLE
            }
        }

        // GiftViewModel 초기화
        giftViewModel = ViewModelProvider(requireActivity()).get(Gificon_ViewModel::class.java)

        // GiftViewModel의 giftList를 관찰하여 데이터가 변경될 때마다 화면 갱신
        giftViewModel.collectGifts.observe(viewLifecycleOwner, { gifts ->
            recyclerViewCollectGiftAdapter.setGiftList(gifts.toMutableList())
            recyclerViewCollectGiftAdapter.notifyDataSetChanged()

            if (gifts.isNullOrEmpty()) {
                noGifticonTextView1.visibility = View.VISIBLE
                noGifticonTextView2.visibility = View.VISIBLE
            } else {
                noGifticonTextView1.visibility = View.GONE
                noGifticonTextView2.visibility = View.GONE
            }

            // 마지막으로 삭제된 경우를 체크
            val lastGift = gifts.lastOrNull()
            val lastIndex = if (lastGift != null) gifts.indexOf(lastGift) + 1 else 0

            if (lastGift != null) {
                if (lastIndex == 0 || !recyclerViewCollectGiftAdapter.contains(lastGift)) {
                    recyclerViewCollectGiftAdapter.removeLastGift()
                    noGifticonTextView1.visibility = View.VISIBLE
                    noGifticonTextView2.visibility = View.VISIBLE
                }
            }
        })
    }
    // 새로운 기프티콘이 추가되었을 때 호출되는 메서드
    private fun onNewGiftAdded(gift: Collect_Gift) {
        recyclerViewCollectGiftAdapter.addGift(gift)
        recyclerView.scrollToPosition(0)  // 목록의 맨 위로 스크롤
        noGifticonTextView1.visibility = View.GONE
        noGifticonTextView2.visibility = View.GONE
    }

    // 활동 결과 처리 런처에서 호출되는 메서드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                val collectGift = it.getSerializableExtra("gift") as? Collect_Gift
                collectGift?.let { onNewGiftAdded(it) }
            }
        }
    }

    // 메뉴를 생성하는 메서드
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collect_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}