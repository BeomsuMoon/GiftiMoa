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

class Home_Fragment_List_Mart : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_home_list_mart, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_mart)
        recyclerViewSearchGiftAdapter = RecyclerViewSearchGiftAdapter(requireActivity() as Search_gift_activity, giftList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewSearchGiftAdapter

        // 이 부분에서 데이터를 초기화하도록 수정
        prepareGiftListData()
    }

    private fun prepareGiftListData() {
        var gift = Search_Gift("GS25", R.drawable.img_brand_gs25)
        giftList.add(gift)
        gift = Search_Gift("CU", R.drawable.img_brand_cu)
        giftList.add(gift)
        gift = Search_Gift("세븐일레븐", R.drawable.img_brand_seveneleven)
        giftList.add(gift)
        gift = Search_Gift("미니스톱", R.drawable.img_ministop)
        giftList.add(gift)
        gift = Search_Gift("이마트", R.drawable.img_brand_emart)
        giftList.add(gift)
        gift = Search_Gift("이마트24", R.drawable.img_emart24)
        giftList.add(gift)
        gift = Search_Gift("롯데마트", R.drawable.img_lottemart)
        giftList.add(gift)
        gift = Search_Gift("홈플러스", R.drawable.img_homeplus)
        giftList.add(gift)
        gift = Search_Gift("하이마트", R.drawable.img_himart)
        giftList.add(gift)
        gift = Search_Gift("GS슈퍼마켓(TheFRESH)", R.drawable.img_brand_gs_the_fresh)
        giftList.add(gift)



        recyclerViewSearchGiftAdapter!!.notifyDataSetChanged()
    }

}
