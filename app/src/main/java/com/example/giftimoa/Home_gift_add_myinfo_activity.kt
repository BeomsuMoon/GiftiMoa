package com.example.giftimoa

import android.view.MenuItem
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutHomeGiftAddInfoBinding
import com.example.giftimoa.databinding.LayoutHomeMygiftEditBinding
import com.example.giftimoa.dto.Home_gift
import com.example.giftimoa.dto.favorite
import java.text.NumberFormat
import java.util.Locale

class Home_gift_add_myinfo_activity : AppCompatActivity() {
    private lateinit var binding : LayoutHomeMygiftEditBinding
    private lateinit var gift: Home_gift
    private lateinit var giftViewModel: Gificon_ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutHomeMygiftEditBinding.inflate(layoutInflater)
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
        //뷰 시작시 생성 정보
        binding.userNickname.text = gift.nickname
        binding.textGiftName.text = gift.h_product_name
        binding.textEffectiveDate.text = gift.h_effectiveDate
        binding.textPrice.text = priceString
        binding.textExpiration.text = gift.h_brand
        binding.textProductDescription.text = gift.h_product_description
        Glide.with(this)
            .load(gift.h_imageUrl)
            .into(binding.uploadImage)


        binding.uploadImage.setOnClickListener {
            showFullscreenImageDialog(gift.h_imageUrl)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_info_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_info_edit -> {
                val intent = Intent(this, Home_gift_edit::class.java)
                intent.putExtra("gift", gift)
                editActivityResultLauncher.launch(intent)
                true
            }

            R.id.home_info_delete -> {
                AlertDialog.Builder(this)
                    .setMessage("기프티콘을 삭제하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        //giftViewModel.deleteGift(gift)
                        finish()
                    }
                    .setNegativeButton("취소", null)
                    .show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}