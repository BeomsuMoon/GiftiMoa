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

class Home_Fragment_List_Chicken : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_home_list_chicken, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_chicken)
        recyclerViewSearchGiftAdapter = RecyclerViewSearchGiftAdapter(requireActivity() as Search_gift_activity, giftList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewSearchGiftAdapter

        // 이 부분에서 데이터를 초기화하도록 수정
        prepareGiftListData()
    }

    private fun prepareGiftListData() {
        var gift = Search_Gift("BBQ", R.drawable.img_brand_bbq)
        giftList.add(gift)
        gift = Search_Gift("BHC", R.drawable.img_brand_bhc)
        giftList.add(gift)
        gift = Search_Gift("굽네치킨", R.drawable.img_brand_goobne)
        giftList.add(gift)
        gift = Search_Gift("60계치킨", R.drawable.img_brand_60chicken)
        giftList.add(gift)
        gift = Search_Gift("교촌치킨", R.drawable.img_brand_kyochon)
        giftList.add(gift)
        gift = Search_Gift("처갓집", R.drawable.img_brand_cyugodgib)
        giftList.add(gift)
        gift = Search_Gift("바른치킨", R.drawable.img_brand_barrenchicken)
        giftList.add(gift)
        gift = Search_Gift("또래오래", R.drawable.img_brand_toreore)
        giftList.add(gift)
        gift = Search_Gift("지코바", R.drawable.img_gcova)
        giftList.add(gift)
        gift = Search_Gift("깐부치킨", R.drawable.img_brand_kkanbu)
        giftList.add(gift)
        gift = Search_Gift("기영이숯불두마리치킨", R.drawable.img_giyoungchicken)
        giftList.add(gift)
        gift = Search_Gift("페리카나", R.drawable.img_pefricana)
        giftList.add(gift)
        gift = Search_Gift("치킨플러스", R.drawable.img_chickenplus)
        giftList.add(gift)
        gift = Search_Gift("피자나라치킨공주", R.drawable.img_pizzanarachicken)
        giftList.add(gift)
        gift = Search_Gift("이춘봉치킨", R.drawable.img_brand_leechunbong_chicken)
        giftList.add(gift)
        gift = Search_Gift("또봉이통닭", R.drawable.img_brand_tobongyee)
        giftList.add(gift)
        gift = Search_Gift("컬투치킨", R.drawable.img_brand_ultochicken)
        giftList.add(gift)
        gift = Search_Gift("푸라닭", R.drawable.img_puradak)
        giftList.add(gift)
        gift = Search_Gift("노랑통닭", R.drawable.img_norangchicken)
        giftList.add(gift)
        gift = Search_Gift("네네치킨", R.drawable.img_nenechicken)
        giftList.add(gift)

        recyclerViewSearchGiftAdapter!!.notifyDataSetChanged()
    }

}
