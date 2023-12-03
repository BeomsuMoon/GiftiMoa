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
import com.example.giftimoa.Collect_Utils
import com.example.giftimoa.R
import com.example.giftimoa.dto.Collect_Gift

class RecyclerViewCollectGiftAdapter constructor(
    private var giftList: MutableList<Collect_Gift>,
    private val itemClickListener: (Collect_Gift) -> Unit
) : RecyclerView.Adapter<RecyclerViewCollectGiftAdapter.MyViewHolder>() {

    fun addGift(gift: Collect_Gift) {
        // 새로운 기프티콘을 목록의 맨 앞에 추가
        giftList.add(0, gift)
        notifyItemInserted(0)
    }

    fun setGiftList(newList: List<Collect_Gift>) {
        giftList.clear()
        giftList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeLastGift() {
        if (giftList.isNotEmpty()) {
            val lastIndex = giftList.size - 1
            giftList.removeAt(lastIndex)
            notifyItemRemoved(lastIndex)
        }
    }

    fun contains(gift: Collect_Gift): Boolean {
        return giftList.contains(gift)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collect_giticard, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val gift = giftList[position]
        val dateString = "${gift.effectiveDate}까지"
        val badge = Collect_Utils.calDday(gift) //남은 기간 D-day

        holder.tv_brand.text = gift.usage
        holder.tv_date.text = dateString
        holder.tv_name.text = gift.giftName
        holder.tv_banner_badge.text = badge.content
        holder.tv_banner_badge.setBackgroundColor(Color.parseColor(badge.color))

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
