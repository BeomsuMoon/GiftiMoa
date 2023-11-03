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
import com.example.giftimoa.Search_gift_activity
import com.example.giftimoa.dto.Collect_Gift

class RecyclerViewCollectGiftAdapter constructor(
    private val getActivity: Search_gift_activity,
    private val giftList: List<Collect_Gift>
) : RecyclerView.Adapter<RecyclerViewCollectGiftAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collect_giticard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gift = giftList[position]
        holder.tv_brand.text = gift.brand
        holder.iv_product_preview.setImageResource(gift.image)

        holder.cardView.setOnClickListener {
            Toast.makeText(getActivity, gift.brand, Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return giftList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_brand: TextView = itemView.findViewById(R.id.tv_brand)
        val iv_product_preview: ImageView = itemView.findViewById(R.id.iv_product_preview)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date)
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_banner_badge: TextView = itemView.findViewById(R.id.tv_banner_badge)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

    }
}
