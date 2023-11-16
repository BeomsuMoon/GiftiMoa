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
import com.example.giftimoa.R
import com.example.giftimoa.Collect_Utils
import com.example.giftimoa.Home_Utils
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.dto.Home_gift

class RecyclerViewHomeGiftAdapter constructor(
    private var h_giftList: MutableList<Home_gift>,
    private val itemClickListener: (Home_gift) -> Unit  // itemClickListener를 추가
) : RecyclerView.Adapter<RecyclerViewHomeGiftAdapter.HomeViewHolder>() {

    fun setGiftList(h_gifts: MutableList<Home_gift>) {
        this.h_giftList = h_gifts
    }

    fun addGift(collectGift: Home_gift) {
        h_giftList.add(collectGift)
        notifyItemInserted(h_giftList.size - 1)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_giftcard, parent, false)
        return HomeViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val gift = h_giftList[position]
        val dateString = "${gift.h_effectiveDate}까지"
        val priceString = "${gift.h_price}원"
        val badge = Home_Utils.calDday(gift) //남은 기간 D-day
        holder.tv_price.text = priceString
        holder.tv_date.text = dateString
        holder.tv_product_name.text = gift.h_product_name
        holder.tv_banner_badge.text = badge.content
        holder.tv_banner_badge.setBackgroundColor(Color.parseColor(badge.color))

        Glide.with(holder.itemView)
            .load(gift.h_imageUrl)
            .into(holder.giftImageView)

        holder.cardView.setOnClickListener {
            itemClickListener(gift)  // 클릭 이벤트가 발생하면 itemClickListener를 호출
        }
    }

    override fun getItemCount(): Int {
        return h_giftList.size
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_product_name: TextView = itemView.findViewById(R.id.tv_product_name) //상품명
        var tv_price: TextView = itemView.findViewById(R.id.tv_price) //가격
        var tv_date: TextView = itemView.findViewById(R.id.tv_date) //유효기간
        var cardView: CardView = itemView.findViewById(R.id.cardView) //카드 뷰
        var giftImageView: ImageView = itemView.findViewById(R.id.iv_product_preview) //이미지
        var tv_banner_badge: TextView = itemView.findViewById(R.id.tv_banner_badge) //남은 기간z
    }
}