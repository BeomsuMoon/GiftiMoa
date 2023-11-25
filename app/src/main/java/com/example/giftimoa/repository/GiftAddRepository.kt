package com.example.giftimoa.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.giftimoa.dto.Collect_Gift
import com.example.giftimoa.dto.Home_gift
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GiftAddRepository(private val context: Context) {

    // MutableLiveData를 사용하여 수집된 기프트 목록을 보관
    private val _giftList: MutableLiveData<List<Collect_Gift>> = MutableLiveData()

    // MutableLiveData를 사용하여 홈 기프트 목록을 보관
    private val _homeGifts: MutableLiveData<List<Home_gift>> = MutableLiveData()

    // LiveData를 통해 수집된 기프트 목록을 외부로 노출
    val giftList: LiveData<List<Collect_Gift>>
        get() = _giftList

    // LiveData를 통해 홈 기프트 목록을 외부로 노출
    val homeGifts: LiveData<List<Home_gift>>
        get() = _homeGifts

    // 서버에서 수집된 기프트 목록을 가져오는 함수
    suspend fun fetchGiftListFromServer(userEmail: String): List<Collect_Gift> {
        var response: Response? = null
        try {
            // 서버에서 수집된 기프트 목록을 가져오기 위한 URL 생성
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
                // 서버 응답을 처리하고 MutableLiveData를 업데이트
                val jsonResponse = response.body?.string()
                Log.d("GiftAddRepository", "Server response: $jsonResponse")
                val giftList = parseGiftList(jsonResponse)
                _giftList.postValue(giftList)
                return giftList
            } else {
                // 서버 오류 처리
                val errorBody = response?.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("서버에서 데이터를 가져오지 못했습니다")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("서버에서 데이터를 가져오지 못했습니다")
        }
    }

    // JSON 응답을 파싱하여 기프트 목록으로 변환하는 함수
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

    // 서버에서 홈 기프트 목록을 가져오는 함수
    suspend fun fetchHomeGiftsFromServer(userEmail: String): List<Home_gift> {
        var response: Response? = null
        try {
            // API에 따라 URL 및 기타 매개변수 조정
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
                // 서버 응답을 처리하고 MutableLiveData를 업데이트
                val jsonResponse = response.body?.string()
                Log.d("GiftAddRepository", "Server response: $jsonResponse")
                val homeGiftsList = parseHomeGifts(jsonResponse)
                _homeGifts.postValue(homeGiftsList)
                return homeGiftsList
            } else {
                // 서버 오류 처리
                val errorBody = response?.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("Failed to fetch home gifts from the server")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("Failed to fetch home gifts from the server")
        }
    }

    // JSON 응답을 파싱하여 홈 기프트 목록으로 변환하는 함수
    private fun parseHomeGifts(jsonResponse: String?): List<Home_gift> {
        val homeGiftList = mutableListOf<Home_gift>()
        try {
            // 홈 기프트를 위한 JSON 응답 파싱
            if (!jsonResponse.isNullOrBlank()) {
                val jsonArray = JSONArray(jsonResponse)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val h_id = jsonObject.getInt("h_id")
                    val h_product_name = jsonObject.getString("h_product_name")
                    val h_effectiveDate = jsonObject.getString("h_effectiveDate")
                    val h_price = jsonObject.getString("h_price")
                    val h_category = jsonObject.getString("h_category")
                    val h_brand = jsonObject.getString("h_brand")
                    val h_product_description = jsonObject.getString("h_product_description")
                    val h_imageUrl = jsonObject.getString("h_imageUrl")
                    val h_state = jsonObject.getInt("h_state")
                    val favorite = jsonObject.getInt("favorite")
                    val nickname = jsonObject.getString("username")

                    val homeGift = Home_gift(
                        h_id,
                        h_product_name,
                        h_effectiveDate,
                        h_price,
                        h_category,
                        h_brand,
                        h_product_description,
                        h_imageUrl,
                        h_state,
                        favorite,
                        nickname
                    )
                    homeGiftList.add(homeGift)
                    Log.d("GiftAddRepository", "Parsed home gift: $homeGift")
                }
            }
        } catch (e: JSONException) {
            Log.e("GiftAddRepository", "Error parsing home gifts JSON: ${e.message}", e)
            throw IOException("Error parsing home gifts JSON: ${e.message}", e)
        }
        return homeGiftList
    }

    // 서버에서 기프트 삭제
    suspend fun deleteGiftFromServer(ID: Int) {
        try {
            // 기프트 삭제를 위한 URL 생성
            val url = "http://3.35.110.246:3306/deleteGift?ID=$ID"
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            val client = OkHttpClient.Builder()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                // 서버 오류 처리
                val errorBody = response.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("Failed to delete gift from the server")
            } else {
                // 성공적으로 삭제된 경우 로그 기록
                Log.d("tlqkf1", "$ID")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("Failed to delete gift from the server")
        }
    }

    // 사용자 이메일을 기반으로 서버에서 기프트 목록을 가져오는 함수
    suspend fun fetchGiftListByEmail(userEmail: String): List<Home_gift> {
        var response: Response? = null
        try {
            // 사용자 이메일을 기반으로 홈 기프트 목록을 가져오기 위한 URL 생성
            val url = "http://3.35.110.246:3306/homeGifts_my?email=$userEmail"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val client = OkHttpClient.Builder()
                .build()

            response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // 서버 응답을 처리하고 MutableLiveData를 업데이트
                val jsonResponse = response.body?.string()
                Log.d("GiftAddRepository", "Server response: $jsonResponse")
                val homeGiftsList = parseHomeGifts(jsonResponse)
                _homeGifts.postValue(homeGiftsList)
                return homeGiftsList
            } else {
                // 서버 오류 처리
                val errorBody = response?.body?.string()
                Log.e("GiftAddRepository", "Server error: $errorBody")
                throw IOException("서버에서 데이터를 가져오지 못했습니다")
            }
        } catch (e: Exception) {
            Log.e("GiftAddRepository", "Exception: ${e.message}", e)
            throw IOException("서버에서 데이터를 가져오지 못했습니다")
        }
    }

    suspend fun fetchBrandGifts(brandName: String): List<Home_gift> = withContext(Dispatchers.IO) {
        val url = URL("http://3.35.110.246:3306/Categorybrand?brand=$brandName")
        val connection = url.openConnection() as HttpURLConnection

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))

            val response = reader.use { it.readText() }
            val gson = Gson()
            val giftType = object : TypeToken<List<Home_gift>>() {}.type
            gson.fromJson<List<Home_gift>>(response, giftType)
        } else {
            emptyList()
        }
    }

}
