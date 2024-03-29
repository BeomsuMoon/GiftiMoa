package com.example.giftimoa.home_fragment_List

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.R
import com.example.giftimoa.dto.Search_Gift
import com.example.giftimoa.adpater_list.RecyclerViewSearchGiftAdapter

class Home_Fragment_List_Movie : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var recyclerViewSearchGiftAdapter: RecyclerViewSearchGiftAdapter? = null
    private var giftList = mutableListOf<Search_Gift>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_list_movie, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_movie)
        recyclerViewSearchGiftAdapter = RecyclerViewSearchGiftAdapter(requireActivity() as Search_gift_activity, giftList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewSearchGiftAdapter

        // 이 부분에서 데이터를 초기화하도록 수정
        prepareGiftListData()
    }

    private fun prepareGiftListData() {
        var gift = Search_Gift("CGV", R.drawable.img_cgv)
        giftList.add(gift)
        gift = Search_Gift("교보문고", R.drawable.img_brand_gyubo)
        giftList.add(gift)
        gift = Search_Gift("롯데시네마", R.drawable.img_brand_lottecynema)
        giftList.add(gift)
        gift = Search_Gift("메가박스", R.drawable.img_brand_megabox)
        giftList.add(gift)
        gift = Search_Gift("영풍문고", R.drawable.img_brand_yp_books)
        giftList.add(gift)
        gift = Search_Gift("컬쳐랜드", R.drawable.img_brand_cultureland)
        giftList.add(gift)
        gift = Search_Gift("해피머니", R.drawable.img_happymoney)
        giftList.add(gift)
        gift = Search_Gift("구글플레이", R.drawable.img_brand_google_giftcard)
        giftList.add(gift)
        gift = Search_Gift("씨네큐", R.drawable.img_cineq)
        giftList.add(gift)
        gift = Search_Gift("인생네컷", R.drawable.img_selfstudio)
        giftList.add(gift)


        recyclerViewSearchGiftAdapter!!.notifyDataSetChanged()
    }

}
