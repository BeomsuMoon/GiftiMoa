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

class Home_Fragment_List_Snack : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_home_list_snack, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_snack)
        recyclerViewSearchGiftAdapter = RecyclerViewSearchGiftAdapter(requireActivity() as Search_gift_activity, giftList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recyclerViewSearchGiftAdapter

        // 이 부분에서 데이터를 초기화하도록 수정
        prepareGiftListData()
    }

    private fun prepareGiftListData() {
        var gift = Search_Gift("애슐리", R.drawable.img_brand_ashley)
        giftList.add(gift)
        gift = Search_Gift("배달의민족", R.drawable.img_brand_bedalminjok)
        giftList.add(gift)
        gift = Search_Gift("본죽", R.drawable.img_brand_bon_porridge)
        giftList.add(gift)
        gift = Search_Gift("본죽&비빔밥", R.drawable.img_brand_bon_porridge_bibimbob)
        giftList.add(gift)
        gift = Search_Gift("본도시락", R.drawable.img_brand_bondosirok)
        giftList.add(gift)
        gift = Search_Gift("이삭토스트", R.drawable.img_brand_isaac_toast)
        giftList.add(gift)
        gift = Search_Gift("아웃백", R.drawable.img_brand_outback)
        giftList.add(gift)
        gift = Search_Gift("돼지게티", R.drawable.img_brand_pig_spagetti)
        giftList.add(gift)
        gift = Search_Gift("서브웨이", R.drawable.img_brand_subway)
        giftList.add(gift)
        gift = Search_Gift("요기요", R.drawable.img_brand_yogiyo)
        giftList.add(gift)
        gift = Search_Gift("VIPS", R.drawable.img_vips)
        giftList.add(gift)
        gift = Search_Gift("샐러디", R.drawable.img_salady)
        giftList.add(gift)
        gift = Search_Gift("죽이야기", R.drawable.img_zukstory)
        giftList.add(gift)
        gift = Search_Gift("죠스떡볶이", R.drawable.img_jawsfood)
        giftList.add(gift)
        gift = Search_Gift("청년다방", R.drawable.img_youngdabang)
        giftList.add(gift)
        gift = Search_Gift("역전우동0410", R.drawable.img_yuckjunnoddle)
        giftList.add(gift)
        gift = Search_Gift("뉴욕야시장", R.drawable.img_newyorkyasijang)
        giftList.add(gift)
        gift = Search_Gift("써브웨이", R.drawable.img_subway)
        giftList.add(gift)
        gift = Search_Gift("생활맥주", R.drawable.img_ssang_bir)
        giftList.add(gift)
        gift = Search_Gift("에그드랍", R.drawable.img_eggdrop)
        giftList.add(gift)

        recyclerViewSearchGiftAdapter!!.notifyDataSetChanged()
    }

}
