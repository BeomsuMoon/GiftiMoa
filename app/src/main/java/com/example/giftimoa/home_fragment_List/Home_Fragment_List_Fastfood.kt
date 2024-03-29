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

class Home_Fragment_List_Fastfood : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_home_list_fastfood, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_fastfood)
        recyclerViewSearchGiftAdapter = RecyclerViewSearchGiftAdapter(requireActivity() as Search_gift_activity, giftList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewSearchGiftAdapter

        // 이 부분에서 데이터를 초기화하도록 수정
        prepareGiftListData()
    }

    private fun prepareGiftListData() {
        var gift = Search_Gift("KFC", R.drawable.img_brand_kfc)
        giftList.add(gift)
        gift = Search_Gift("롯데리아", R.drawable.img_brand_lottelia)
        giftList.add(gift)
        gift = Search_Gift("버거킹", R.drawable.img_brand_buggerking)
        giftList.add(gift)
        gift = Search_Gift("맥도날드", R.drawable.img_brand_macdonald)
        giftList.add(gift)
        gift = Search_Gift("프랭크버거", R.drawable.img_frankbuger)
        giftList.add(gift)
        gift = Search_Gift("뉴욕버거", R.drawable.img_newyork)
        giftList.add(gift)
        gift = Search_Gift("맘스터치", R.drawable.img_momstouch)
        giftList.add(gift)
        gift = Search_Gift("버거리", R.drawable.img_bugerry)
        giftList.add(gift)
        gift = Search_Gift("이삭토스트", R.drawable.img_isactoast)
        giftList.add(gift)
        gift = Search_Gift("테그42", R.drawable.img_tag42)
        giftList.add(gift)
        gift = Search_Gift("샐러디", R.drawable.img_salady)
        giftList.add(gift)
        gift = Search_Gift("에그드랍", R.drawable.img_eggdrop)
        giftList.add(gift)
        gift = Search_Gift("와플대학", R.drawable.img_waffle)
        giftList.add(gift)

        recyclerViewSearchGiftAdapter!!.notifyDataSetChanged()
    }

}
