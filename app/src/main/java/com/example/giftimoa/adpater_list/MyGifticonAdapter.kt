package com.example.giftimoa.adpater_list

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giftimoa.Home_Utils
import com.example.giftimoa.R
import com.example.giftimoa.dto.Home_gift
import java.text.NumberFormat
import java.util.Locale

class MyGifticonAdapter(
    private var myGiftList: MutableList<Home_gift>,
    private val itemClickListener: (Home_gift) -> Unit
) : RecyclerView.Adapter<MyGifticonAdapter.MyGifticonViewHolder>() {

    // 뷰홀더 클래스 정의
    inner class MyGifticonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_product_name: TextView = itemView.findViewById(R.id.tv_product_name)
        var tv_price: TextView = itemView.findViewById(R.id.tv_price)
        var tv_date: TextView = itemView.findViewById(R.id.tv_date)
        var cardView: CardView = itemView.findViewById(R.id.cardView)
        var giftImageView: ImageView = itemView.findViewById(R.id.iv_product_preview)
        var tv_banner_badge: TextView = itemView.findViewById(R.id.tv_banner_badge)
    }

    // onCreateViewHolder: 뷰홀더를 생성하고 레이아웃을 연결하는 역할
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGifticonViewHolder {
        // XML 레이아웃을 인플레이션하여 뷰홀더 객체를 생성
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_giftcard, parent, false)
        return MyGifticonViewHolder(view)
    }

    // onBindViewHolder: 뷰홀더에 데이터를 바인딩하는 역할
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyGifticonViewHolder, position: Int) {
        // 현재 위치의 기프트 객체 가져오기
        val gift = myGiftList[position]

        // 날짜 및 가격 문자열 생성
        val dateString = "${gift.h_effectiveDate}까지"
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        val priceString = "${numberFormat.format(gift.h_price.toInt())}원"

        // D-day 계산 및 배지 색상 설정
        val badge = Home_Utils.calDday(gift)

        // 뷰홀더에 데이터 설정
        holder.tv_price.text = priceString
        holder.tv_date.text = dateString
        holder.tv_product_name.text = gift.h_product_name
        holder.tv_banner_badge.text = badge.content
        holder.tv_banner_badge.setBackgroundColor(Color.parseColor(badge.color))

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView)
            .load(gift.h_imageUrl)
            .into(holder.giftImageView)

        // 아이템 클릭 리스너 설정
        holder.cardView.setOnClickListener {
            itemClickListener(gift)
        }
    }

    // getItemCount: 데이터 목록의 크기 반환
    override fun getItemCount(): Int {
        return myGiftList.size
    }

    // setMyGiftList: 기프트 목록 갱신 및 데이터 변경 알림
    fun setMyGiftList(myGifts: List<Home_gift>) {
        this.myGiftList = myGifts.toMutableList()
        notifyDataSetChanged()
    }
}
