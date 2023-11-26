package com.example.giftimoa.adpater_list

import android.graphics.Color
import android.os.Build
import android.util.Log
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
import com.example.giftimoa.dto.Collect_Gift

class RecyclerViewCollectGiftAdapter constructor(
    private var giftList: MutableList<Collect_Gift>,
    private val itemClickListener: (Collect_Gift) -> Unit
) : RecyclerView.Adapter<RecyclerViewCollectGiftAdapter.MyViewHolder>() {

    fun setGiftList(newList: List<Collect_Gift>) {
        giftList.clear()
        giftList.addAll(newList)
        notifyDataSetChanged()
    }
    fun addGift(collectGift: Collect_Gift) {
        giftList.add(collectGift)
        notifyItemInserted(giftList.size - 1)
    }


    fun removeLastGift() {
        if (giftList.isNotEmpty()) {
            val lastGift = giftList.last()
            val lastIndex = giftList.indexOf(lastGift)
            giftList.removeAt(lastIndex)
            notifyItemRemoved(lastIndex)
        }
    }

/*
    fun addGift(collectGift: Collect_Gift) {
        if (giftList.isEmpty()) {
            giftList.clear()
            notifyDataSetChanged()
            return
        }else{
        giftList.add(collectGift)
        notifyItemInserted(giftList.size - 1)
        }
    }
*/

    fun contains(gift: Collect_Gift): Boolean {
        return giftList.contains(gift)
    }

    fun updateGift(updatedGift: Collect_Gift) {
        val existingGift = giftList.find { it.ID == updatedGift.ID }

        if (existingGift != null) {
            // 아이템이 이미 목록에 있는 경우
            val position = giftList.indexOf(existingGift)
            giftList[position] = updatedGift
            notifyItemChanged(position)
        } else {
            // 아이템이 목록에 없는 경우
            // 추가할지 여부를 물어본 후 추가하거나 무시할 수 있습니다.
            // 여기서는 추가하도록 하였습니다.
            addGift(updatedGift)
        }
    }

    fun deleteGift(gift: Collect_Gift) {
        val position = giftList.indexOf(gift)
        if (position != -1) {
            giftList.removeAt(position)
            notifyItemRemoved(position)
        }

        // 마지막 기프티콘이 남아있을 때도 삭제할 수 있도록 조건 추가
        if (position == giftList.size && giftList.isNotEmpty()) {
            val lastGift = giftList.last()
            val lastIndex = giftList.indexOf(lastGift)
            giftList.removeAt(lastIndex)
            notifyItemRemoved(lastIndex)
        }
    }


    // 새로운 데이터를 설정하는 메서드 추가
    fun setGiftListFromExternalDB(gifts: List<Collect_Gift>) {
        this.giftList.clear() // 기존 목록 비우기
        this.giftList.addAll(gifts) // 외부 DB에서 가져온 목록으로 채우기
        notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알림
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
