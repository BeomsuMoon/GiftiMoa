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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val collectGift = result.data!!.extras!!.get("gift") as Collect_Gift
                giftViewModel.addGift(collectGift)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        giftViewModel = ViewModelProvider(requireActivity()).get(Gificon_ViewModel::class.java)
    }

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

        private fun startCollectGiftAddActivity() {
        val intent = Intent(requireContext(), Collect_gift_add_activity::class.java)
        activityResult.launch(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noGifticonTextView1 = view.findViewById<TextView>(R.id.tv_noGifticon1)
        noGifticonTextView2 = view.findViewById<TextView>(R.id.tv_noGifticon2)
        recyclerView = view.findViewById<RecyclerView>(R.id.rv_Gift_Collect)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView.layoutManager = layoutManager

        recyclerViewCollectGiftAdapter = RecyclerViewCollectGiftAdapter(mutableListOf()) { gift ->
            val intent = Intent(requireContext(), Collect_gift_add_info_activity::class.java)
            intent.putExtra("gift", gift)
            startActivity(intent)
        }

        recyclerView.adapter = recyclerViewCollectGiftAdapter

        view.findViewById<FloatingActionButton>(R.id.fab_btn).setOnClickListener {
            startCollectGiftAddActivity()
        }

        // 코루틴을 사용하여 fetchGiftListFromRepository 호출
        viewLifecycleOwner.lifecycleScope.launch {
            val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("user_email", null)
            if (userEmail != null && userEmail.isNotEmpty()) {
                giftViewModel.fetchGiftListFromRepository(requireContext(), userEmail)
            } else {
                Log.d("test", "test : $userEmail")
            }
        }

        giftViewModel.collectGifts.observe(viewLifecycleOwner, { gifts ->
            recyclerViewCollectGiftAdapter.setGiftList(gifts.toMutableList())
            recyclerViewCollectGiftAdapter.notifyDataSetChanged()

            if (gifts.isEmpty()) {
                noGifticonTextView1.visibility = View.VISIBLE
                noGifticonTextView2.visibility = View.VISIBLE
            } else {
                noGifticonTextView1.visibility = View.GONE
                noGifticonTextView2.visibility = View.GONE
            }
        })

    }

    private fun setupRecyclerView() {
        recyclerViewCollectGiftAdapter = RecyclerViewCollectGiftAdapter(mutableListOf()) { gift ->
            val intent = Intent(requireContext(), Collect_gift_add_info_activity::class.java)
            intent.putExtra("gift", gift)
            startActivity(intent)
        }
        recyclerView.adapter = recyclerViewCollectGiftAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collect_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}



