package com.example.giftimoa

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutHomeGiftAddInfoBinding
import com.example.giftimoa.dto.Home_gift
import com.example.giftimoa.dto.favorite
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

class Home_gift_add_info_activity : AppCompatActivity() {
    private lateinit var binding : LayoutHomeGiftAddInfoBinding
    private lateinit var gift: Home_gift
    private lateinit var giftViewModel: Gificon_ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutHomeGiftAddInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        //액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 정보"
        gift = intent.getSerializableExtra("gift") as Home_gift

        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        val priceString = "${numberFormat.format(gift.h_price.toInt())}원"
        val effectiveDate = "${gift.h_effectiveDate}까지"
        //뷰 시작시 생성 정보
        binding.userNickname.text = gift.nickname
        binding.textGiftName.text = gift.h_product_name
        binding.textEffectiveDate.text = effectiveDate
        binding.textPrice.text = priceString
        binding.textExpiration.text = gift.h_brand
        binding.textProductDescription.text = gift.h_product_description
        Glide.with(this)
            .load(gift.h_imageUrl)
            .into(binding.uploadImage)

        // 좋아요 클릭 시
        binding.favoriteClk.setOnClickListener {
            Log.d("로그","찜 추가 클릭")
            toggleFavorite(gift)
            updateFavoriteImage(gift)
        }

        binding.uploadImage.setOnClickListener {
            showFullscreenImageDialog(gift.h_imageUrl)
        }

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "")

        binding.chatBtn.setOnClickListener {
            val nickname = binding.userNickname.text.toString()

            val client = OkHttpClient()
            val url = "http://3.35.110.246:3306/getNicknameByEmail" // 서버의 닉네임 확인 엔드포인트

            // 이메일이 null이 아니면 서버에 요청을 보냄
            if (!userEmail.isNullOrBlank()) {
                val json = """{"email": "$userEmail"}"""
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val responseData = response.body?.string()
                        runOnUiThread {
                            // responseData에 있는 데이터를 mynickname 변수에 저장
                            val mynickname = responseData?.takeIf { it.isNotBlank() } ?: ""
                            val formattedNickname = mynickname.replace("\"", "")

                            // 이후의 동작은 이 안에서 수행
                            val intent = Intent(this@Home_gift_add_info_activity, Chatting_room_activity::class.java)
                            intent.putExtra("nickname", gift.nickname)
                            intent.putExtra("brand", gift.h_brand)
                            intent.putExtra(Chatting_room_activity.USERNAME, gift.nickname)
                            intent.putExtra(Chatting_room_activity.USERNAME, formattedNickname)

                            startActivity(intent)
                        }
                    }
                })
            }
        }
    }

    private fun getNicknameFromServer(userEmail: String?) {
        val client = OkHttpClient()
        val url = "http://3.35.110.246:3306/getNicknameByEmail" // 서버의 닉네임 확인 엔드포인트

        // 이메일이 null이 아니면 서버에 요청을 보냄
        if (!userEmail.isNullOrBlank()) {
            val json = """{"email": "$userEmail"}"""
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        val intent = Intent(this@Home_gift_add_info_activity, Chatting_room_activity::class.java)
                        intent.putExtra("nickname", responseData)

                    }
                }
            })
        }
    }

    fun toggleFavorite(homeGift: Home_gift) {
        homeGift.favorite = if (homeGift.favorite == 0) 1 else 0
        if (homeGift.favorite == 1) {
            // 찜한 목록에 추가
            favorite.FavoriteGifts.list.add(homeGift)
        } else {
            // 찜한 목록에서 제거
            favorite.FavoriteGifts.list.remove(homeGift)
        }
        updateFavoriteImage(homeGift)
    }

    fun updateFavoriteImage(homeGift: Home_gift) {
        if (homeGift.favorite == 1) {
            binding.favoriteClk.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.favoriteClk.setImageResource(R.drawable.ic_favorite_outline)
        }
    }

    private val editActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedGift = result.data?.getSerializableExtra("updatedGift") as? Home_gift
            if (updatedGift != null) {
                gift = updatedGift
                // 여기에서 updatedGift를 화면에 업데이트합니다.
                binding.userNickname.text = gift.nickname
                binding.textGiftName.text = gift.h_product_name
                binding.textEffectiveDate.text = gift.h_effectiveDate
                binding.textPrice.text = gift.h_price
                binding.textExpiration.text = gift.h_brand
                binding.textProductDescription.text = gift.h_product_description
            }
        }
    }

    //이미지 클릭시 이미지 전체 화면 보기
    fun showFullscreenImageDialog(imageUrl: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_image, null)
        val dialogImage = dialogLayout.findViewById<ImageView>(R.id.dialog_image)

        Glide.with(this)
            .load(imageUrl)
            .into(dialogImage)

        val dialog = builder.setView(dialogLayout)
            .create()

        dialogImage.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }
}