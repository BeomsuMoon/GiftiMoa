package com.example.giftimoa.repository

import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.Collect_Gift

class GiftAddRepository {
    private lateinit var _giftList: MutableLiveData<List<Collect_Gift>>
}