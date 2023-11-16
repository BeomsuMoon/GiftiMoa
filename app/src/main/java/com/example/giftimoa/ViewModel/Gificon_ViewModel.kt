package com.example.giftimoa.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.dto.Home_gift


class Gificon_ViewModel : ViewModel() {
    private val _collectGifts = MutableLiveData<List<Collect_Gift>>(emptyList())
    val collectGifts: LiveData<List<Collect_Gift>> get() = _collectGifts

    private val _homeGifts = MutableLiveData<List<Home_gift>>(emptyList())
    val homeGifts: LiveData<List<Home_gift>> get() = _homeGifts

    // 새로운 기프트 추가
    fun addGift(gift: Collect_Gift) {
        val currentGifts = _collectGifts.value?.toMutableList() ?: mutableListOf()
        currentGifts.add(gift)
        _collectGifts.value = currentGifts
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

    // 새로운 홈 기프트 추가
    fun addGift(gift: Home_gift) {
        val currentGifts = _homeGifts.value?.toMutableList() ?: mutableListOf()
        currentGifts.add(gift)
        _homeGifts.value = currentGifts
    }

    // 홈 기프트 업데이트
    fun updateGift(gift: Home_gift) {
        val currentGifts = _homeGifts.value?.toMutableList() ?: mutableListOf()
        val index = currentGifts.indexOfFirst { it.h_id == gift.h_id }
        if (index != -1) {
            currentGifts[index] = gift
            _homeGifts.value = currentGifts
        }
    }

    // 홈 기프트 삭제
    fun deleteGift(gift: Home_gift) {
        val currentGifts = _homeGifts.value?.toMutableList() ?: mutableListOf()
        if (currentGifts.remove(gift)) {
            _homeGifts.value = currentGifts
        }
    }

    // 찜한 기프트 가져오기
    fun getFavoriteGifts(): LiveData<List<Home_gift>> {
        return homeGifts // 수정된 부분
    }
}


