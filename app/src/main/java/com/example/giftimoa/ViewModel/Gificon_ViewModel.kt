package com.example.giftimoa.ViewModel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.dto.Home_gift
import com.example.giftimoa.repository.GiftAddRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Gificon_ViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application
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

    private val _giftList = MutableLiveData<List<Collect_Gift>>()
    val giftList: LiveData<List<Collect_Gift>> get() = _giftList

    fun updateGift(updatedGift: Collect_Gift) {
        val currentList = _giftList.value.orEmpty().toMutableList()
        val position = currentList.indexOfFirst { it.ID == updatedGift.ID }
        if (position != -1) {
            currentList[position] = updatedGift
            _giftList.value = currentList

        }
    }

    // 홈 기프트 업데이트
     fun Home_updateGift(gift: Home_gift) {
          val currentGifts = _homeGifts.value?.toMutableList() ?: mutableListOf()
          val index = currentGifts.indexOfFirst { it.h_id == gift.h_id }
          if (index != -1) {
              currentGifts[index] = gift
              _homeGifts.value = currentGifts
          }
      }

    // GiftAddRepository를 통해 데이터를 가져오는 함수 추가
    fun fetchHomeGiftsFromRepository(context: Context, userEmail: String) {
        viewModelScope.launch {
            try {
                // 백그라운드 스레드에서 GiftAddRepository를 통해 데이터를 가져오기
                val homeGifts = withContext(Dispatchers.IO) {
                    GiftAddRepository(context).fetchHomeGiftsFromServer(userEmail)
                }

                // LiveData에 업데이트
                _homeGifts.postValue(homeGifts)
            } catch (e: Exception) {
                Log.e("Gificon_ViewModel", "Error fetching home gifts: ${e.message}", e)
                // 오류 처리
            }
        }
    }

    fun fetchGiftListFromRepository(context: Context, userEmail: String) {
        viewModelScope.launch {
            try {
                // 백그라운드 스레드에서 GiftAddRepository를 통해 데이터를 가져오기
                val gifts = withContext(Dispatchers.IO) {
                    GiftAddRepository(context).fetchGiftListFromServer(userEmail)
                }
                // LiveData에 업데이트
                _collectGifts.postValue(gifts)
            } catch (e: Exception) {
                Log.e("Gificon_ViewModel", "Error fetching data: ${e.message}", e)
                // 오류 처리
            }
        }
    }

    fun fetchHomeGiftListFromRepository(context: Context, userEmail: String) {
        viewModelScope.launch {
            try {
                // 백그라운드 스레드에서 GiftAddRepository를 통해 데이터를 가져오기
                val gifts = withContext(Dispatchers.IO) {
                    GiftAddRepository(context).fetchHomeGiftsFromServer(userEmail)
                }
                // LiveData에 업데이트
                _homeGifts.postValue(gifts)
            } catch (e: Exception) {
                Log.e("Gificon_ViewModel_homeGifts", "Error fetching data: ${e.message}", e)
                // 오류 처리
            }
        }
    }
    // collect_gift 기프트 삭제
    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteGift(gift: Collect_Gift) {
        viewModelScope.launch {
            try {
                // 서버에서 기프트 삭제
                withContext(Dispatchers.IO) {
                    GiftAddRepository(context).deleteGiftFromServer(gift.ID)
                }

                // 로컬 LiveData에서도 삭제
                val currentGifts = _collectGifts.value?.toMutableList() ?: mutableListOf()
                currentGifts.removeIf { it.ID == gift.ID }
                Log.d("tlqkf", "$gift.ID")

                _collectGifts.value = currentGifts

            } catch (e: Exception) {
                Log.e("Gificon_ViewModel", "Error deleting gift: ${e.message}", e)
                // 오류 처리
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun deletehomeGift(homeGift: Home_gift) {
        viewModelScope.launch {
            try {
                // 서버에서 기프트 삭제
                withContext(Dispatchers.IO) {
                    GiftAddRepository(context).deletehomeGiftFromServer(homeGift.h_id)
                }

                // 로컬 LiveData에서도 삭제
                val currentGifts = _homeGifts.value?.toMutableList() ?: mutableListOf()
                currentGifts.removeIf { it.h_id == homeGift.h_id }

                _homeGifts.value = currentGifts

            } catch (e: Exception) {
                Log.e("Gificon_ViewModel", "Error deleting gift: ${e.message}", e)
                // 오류 처리
            }
        }
    }


    // 새로운 홈 기프트 추가
    fun addGift(gift: Home_gift) {
        val currentGifts = _homeGifts.value?.toMutableList() ?: mutableListOf()
        currentGifts.add(gift)
        _homeGifts.value = currentGifts
    }


    fun fetchBrandGifts(brandName: String) {
        viewModelScope.launch {
            try {
                val gifts = withContext(Dispatchers.IO) {
                    GiftAddRepository(context).fetchBrandGifts(brandName)
                }

                _homeGifts.postValue(gifts)
            } catch (e: Exception) {
                Log.e("Gificon_ViewModel", "Error fetching brand gifts: ${e.message}", e)
            }
        }
    }
}
