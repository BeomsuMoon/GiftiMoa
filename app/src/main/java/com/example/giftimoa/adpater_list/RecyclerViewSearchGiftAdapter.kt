package com.example.giftimoa.adpater_list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.giftimoa.Home_category_brand_list
import com.example.giftimoa.R
import com.example.giftimoa.home_fragment_List.Search_gift_activity
import com.example.giftimoa.dto.Search_Gift

class RecyclerViewSearchGiftAdapter constructor(
    private val getActivity: Search_gift_activity,
    private val giftList: List<Search_Gift>
) : RecyclerView.Adapter<RecyclerViewSearchGiftAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_giftcard, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var gift = giftList[position]
        holder.tv_brand.text = gift.title
        holder.iv_product_preview.setImageResource(gift.image)

        holder.cardView.setOnClickListener {
            val intent = Intent(getActivity, Home_category_brand_list::class.java)
            intent.putExtra("brand_name", gift.title)
            getActivity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return giftList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_brand: TextView = itemView.findViewById(R.id.tv_brand)
        val iv_product_preview: ImageView = itemView.findViewById(R.id.iv_product_preview)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}
