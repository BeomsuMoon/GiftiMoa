package com.example.giftimoa.adpater_list

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giftimoa.R
import com.example.giftimoa.Utils
import com.example.giftimoa.dto.Collect_Gift

class RecyclerViewCollectGiftAdapter constructor(
    private var giftList: MutableList<Collect_Gift>,
    private val itemClickListener: (Collect_Gift) -> Unit  // itemClickListener를 추가
) : RecyclerView.Adapter<RecyclerViewCollectGiftAdapter.MyViewHolder>() {

    fun setGiftList(gifts: MutableList<Collect_Gift>) {
        this.giftList = gifts
    }

    fun addGift(collectGift: Collect_Gift) {
        giftList.add(collectGift)
        notifyItemInserted(giftList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collect_giticard, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val gift = giftList[position]
        holder.tv_brand.text = gift.usage
        holder.tv_date.text = gift.effectiveDate
        holder.tv_name.text = gift.giftName

        // 남은 기간을 계산하고 표시합니다.
        val badge = Utils.calDday(gift)
        holder.tv_banner_badge.text = badge.content
        holder.tv_banner_badge.setTextColor(Color.parseColor(badge.color))

        Glide.with(holder.itemView)
            .load(gift.imageUrl)
            .into(holder.giftImageView)

        holder.cardView.setOnClickListener {
            itemClickListener(gift)  // 클릭 이벤트가 발생하면 itemClickListener를 호출
        }
    }

    override fun getItemCount(): Int {
        return giftList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_brand: TextView = itemView.findViewById(R.id.tv_brand)
        var tv_date: TextView = itemView.findViewById(R.id.tv_date)
        var tv_name: TextView = itemView.findViewById(R.id.tv_name)
        var cardView: CardView = itemView.findViewById(R.id.cardView)
        var giftImageView: ImageView = itemView.findViewById(R.id.iv_product_preview)
        var tv_banner_badge: TextView = itemView.findViewById(R.id.tv_banner_badge)
    }
}
