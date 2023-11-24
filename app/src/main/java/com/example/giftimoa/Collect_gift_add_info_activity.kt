package com.example.giftimoa

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutCollectGiftAddInfoBinding
import com.example.giftimoa.dto.Collect_Gift

class Collect_gift_add_info_activity : AppCompatActivity() {
    private lateinit var binding : LayoutCollectGiftAddInfoBinding
    private lateinit var gift: Collect_Gift
    private lateinit var giftViewModel: Gificon_ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutCollectGiftAddInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        //액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 정보"

        gift = intent.getSerializableExtra("gift") as Collect_Gift


        var barcode = gift.barcode
        barcode = barcode.chunked(4).joinToString(" ")

        binding.textGiftName.text = gift.giftName
        binding.textEffectiveDate.text = gift.effectiveDate
        binding.textBarcode.text = barcode
        binding.textUsage.text = gift.usage


        Glide.with(this)
            .load(gift.imageUrl)
            .into(binding.uploadImage)

        binding.uploadImage.setOnClickListener {
            showFullscreenImageDialog(gift.imageUrl)
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
        menuInflater.inflate(R.menu.collect_info_menu, menu)
        return true
    }

    // 기프트 삭제
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.collect_info_edit -> {
                val intent = Intent(this, Collect_gift_edit_activity::class.java)
                intent.putExtra("gift", gift)
                editActivityResultLauncher.launch(intent)
                true
            }

            R.id.collect_info_delete -> {
                AlertDialog.Builder(this)
                    .setMessage("기프티콘을 삭제하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        giftViewModel.deleteGift(gift)
                        finish()
                    }
                    .setNegativeButton("취소", null)
                    .show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    private val editActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedGift = result.data?.getSerializableExtra("modifiedGift") as? Collect_Gift
            if (updatedGift != null) {
                gift = updatedGift
                // 여기에서 updatedGift를 화면에 업데이트합니다.
                binding.textGiftName.text = gift.giftName
                binding.textEffectiveDate.text = gift.effectiveDate
                binding.textBarcode.text = gift.barcode
                binding.textUsage.text = gift.usage
                // 에러가 발생하지 않아야 합니다.
            }
        }
    }
}
