package com.example.giftimoa.adpater_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.R
import com.example.giftimoa.dto.Collect_Gift

class RecyclerViewCollectGiftAdapter constructor(
    private var giftList: MutableList<Collect_Gift>  // giftList를 MutableList로 선언
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val gift = giftList[position]
        holder.tv_brand.text = gift.usage
        holder.tv_date.text = gift.effectiveDate
        /*        holder.iv_product_preview.setImageResource(gift.img)*/
        holder.tv_name.text = gift.giftName

        holder.cardView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return giftList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_brand: TextView = itemView.findViewById(R.id.tv_brand)
        var tv_date: TextView = itemView.findViewById(R.id.tv_date)
        //        var iv_product_preview: ImageView = itemView.findViewById(R.id.iv_product_preview)
        var tv_name: TextView = itemView.findViewById(R.id.tv_name)
        var cardView: CardView = itemView.findViewById(R.id.cardView)
        //var tv_banner_badge: TextView = itemView.findViewById(R.id.tv_banner_badge)
    }
}
