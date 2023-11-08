package com.example.giftimoa.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.giftimoa.dto.Collect_Gift

class Gificon_ViewModel : ViewModel() {
    val collectGifts: MutableLiveData<MutableList<Collect_Gift>> = MutableLiveData(mutableListOf())

    // 새로운 기프트 추가
    fun addGift(gift: Collect_Gift) {
        val currentGifts = collectGifts.value?.toMutableList() ?: mutableListOf()
        currentGifts.add(gift)
        collectGifts.postValue(currentGifts)
        Log.d("로그", "New gift added: $gift. Total gifts: ${currentGifts.size}")
    }


}
