package com.example.giftimoa.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.giftimoa.dto.Collect_Gift

class Gificon_ViewModel : ViewModel() {
    private val _collectGifts = MutableLiveData<List<Collect_Gift>>(emptyList())
    val collectGifts: LiveData<List<Collect_Gift>> get() = _collectGifts

    // 새로운 기프트 추가
    fun addGift(gift: Collect_Gift) {
        val currentGifts = _collectGifts.value?.toMutableList() ?: mutableListOf()
        currentGifts.add(gift)
        _collectGifts.value = currentGifts
        Log.d("로그", "New gift added: $gift. Total gifts: ${currentGifts.size}")
    }

    // 기프트 업데이트
    fun updateGift(gift: Collect_Gift) {
        val currentGifts = _collectGifts.value?.toMutableList() ?: mutableListOf()
        val index = currentGifts.indexOfFirst { it.id == gift.id }
        if (index != -1) {
            currentGifts[index] = gift
            _collectGifts.value = currentGifts
        }
    }

    // 기프트 삭제
    fun deleteGift(gift: Collect_Gift) {
        val currentGifts = _collectGifts.value?.toMutableList() ?: mutableListOf()
        if (currentGifts.remove(gift)) {
            _collectGifts.value = currentGifts
        }
    }
}

