package com.example.giftimoa.adpater_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView // Import CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.R
import com.example.giftimoa.home_fragment_List.Search_gift_activity
import com.example.giftimoa.dto.Collect_Gift

class RecyclerViewCollectGiftAdapter constructor(
    private val giftList: List<Collect_Gift>
) : RecyclerView.Adapter<RecyclerViewCollectGiftAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collect_giticard, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val gift = giftList[position]
        holder.tv_brand.text = gift.brand
        holder.tv_date.text = gift.date
        holder.iv_product_preview.setImageResource(gift.img)
        holder.tv_name.text = gift.giftName

        holder.cardView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return giftList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_brand: TextView = itemView.findViewById(R.id.tv_brand)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date)
        val iv_product_preview: ImageView = itemView.findViewById(R.id.iv_product_preview)
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
