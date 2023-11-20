package com.example.giftimoa.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.dto.Home_gift
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class GiftAddRepository(private val context: Context) {

    private val _giftList: MutableLiveData<List<Collect_Gift>> = MutableLiveData()

    private val _homeGifts: MutableLiveData<List<Home_gift>> = MutableLiveData()
    val giftList: LiveData<List<Collect_Gift>>
        get() = _giftList

    val HomeGift: LiveData<List<Home_gift>>
        get() = _homeGifts


    suspend fun fetchGiftListFromServer(userEmail: String): List<Collect_Gift> {
        var response: Response? = null
        try {
            Log.d("GiftAddRepository", "fetchGiftListFromServer - userEmail: $userEmail")
            val url = "http://3.35.110.246:3306/getGiftList?email=$userEmail"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val client = OkHttpClient.Builder()
                .build()

            response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val jsonResponse = response.body?.string()
                Log.d("GiftAddRepository", "Server response: $jsonResponse")
                val giftList = parseGiftList(jsonResponse)
                _giftList.postValue(giftList)
                return giftList
            } else {
                val errorBody = response?.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("서버에서 데이터를 가져오지 못했습니다")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("서버에서 데이터를 가져오지 못했습니다")
        }
    }

    private fun parseGiftList(jsonResponse: String?): List<Collect_Gift> {
        val giftList = mutableListOf<Collect_Gift>()
        try {
            if (!jsonResponse.isNullOrBlank()) {
                val jsonArray = JSONArray(jsonResponse)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getInt("id")
                    val giftName = jsonObject.getString("gift_name")
                    val effectiveDate = jsonObject.getString("effective_date")
                    val barcode = jsonObject.getString("barcode")
                    val usage = jsonObject.getString("usage_description")
                    val imageUrl = jsonObject.getString("image_url")
                    val state = jsonObject.getInt("state")

                    val collectGift = Collect_Gift(
                        id,
                        giftName,
                        effectiveDate,
                        barcode,
                        usage,
                        imageUrl,
                        state
                    )
                    giftList.add(collectGift)
                    Log.d("GiftAddRepository", "Parsed gift: $collectGift")
                }
            }
        } catch (e: JSONException) {
            Log.e("GiftAddRepository", "Error parsing JSON: ${e.message}", e)
            throw IOException("Error parsing JSON: ${e.message}", e)
        }
        return giftList
    }

    // Hoom 기프티콘 추가
    suspend fun fetchHomeGiftsFromServer(userEmail: String): List<Home_gift> {
        var response: Response? = null
        try {
            // Adjust the URL and other parameters according to your API
            Log.d("GiftAddRepository", "fetchHomeGiftsFromServer - userEmail: $userEmail")
            val url = "http://3.35.110.246:3306/homeGifts"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val client = OkHttpClient.Builder()
                .build()

            response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val jsonResponse = response.body?.string()
                Log.d("GiftAddRepository", "Server response: $jsonResponse")
                val homeGiftsList = parseHomeGifts(jsonResponse)
                _homeGifts.postValue(homeGiftsList)
                return homeGiftsList
            } else {
                val errorBody = response?.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("Failed to fetch home gifts from the server")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("Failed to fetch home gifts from the server")
        }
    }
    private fun parseHomeGifts(jsonResponse: String?): List<Home_gift> {
        val HomeGift = mutableListOf<Home_gift>()
        try {
            // Parse the JSON response for home gifts
            if (!jsonResponse.isNullOrBlank()) {
                val jsonArray = JSONArray(jsonResponse)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val h_id = jsonObject.getInt("h_id")
                    val h_product_name = jsonObject.getString("h_product_name")
                    val h_effectiveDate = jsonObject.getString("h_effectiveDate")
                    val h_price = jsonObject.getString("h_price")
                    val h_brand = jsonObject.getString("h_brand")
                    val h_product_description = jsonObject.getString("h_product_description")
                    val h_imageUrl = jsonObject.getString("h_imageUrl")
                    val h_state = jsonObject.getInt("h_state")
                    val favorite = jsonObject.getInt("favorite")

                    val homeGift = Home_gift(
                        h_id,
                        h_product_name,
                        h_effectiveDate,
                        h_price,
                        h_brand,
                        h_product_description,
                        h_imageUrl,
                        h_state,
                        favorite
                    )
                    HomeGift.add(homeGift)
                    Log.d("GiftAddRepository", "Parsed home gift: $homeGift")
                }
            }
        } catch (e: JSONException) {
            Log.e("GiftAddRepository", "Error parsing home gifts JSON: ${e.message}", e)
            throw IOException("Error parsing home gifts JSON: ${e.message}", e)
        }
        return HomeGift
    }
    suspend fun deleteGiftFromServer(ID: Int) {
        try {
            val url = "http://3.35.110.246:3306/deleteGift?ID=$ID"
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            val client = OkHttpClient.Builder()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("Failed to delete gift from the server")
            }
            else{
                Log.d("tlqkf1","$ID")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("Failed to delete gift from the server")
        }
    }

}
