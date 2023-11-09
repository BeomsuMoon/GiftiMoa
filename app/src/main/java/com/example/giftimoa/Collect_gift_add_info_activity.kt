package com.example.giftimoa

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
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

    private val editActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedGift = result.data?.getSerializableExtra("updatedGift") as? Collect_Gift
            if (updatedGift != null) {
                gift = updatedGift
                // 여기에서 updatedGift를 화면에 업데이트합니다.
                binding.textGiftName.text = gift.giftName
                binding.textEffectiveDate.text = gift.effectiveDate
                binding.textBarcode.text = gift.barcode
                binding.textUsage.text = gift.usage
            }
        }
    }

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

        binding.textGiftName.text = gift.giftName
        binding.textEffectiveDate.text = gift.effectiveDate
        binding.textBarcode.text = gift.barcode
        binding.textUsage.text = gift.usage

        Glide.with(this)
            .load(gift.imageUrl)
            .into(binding.uploadImage)

        //이미지 클릭시 이미지 전체 화면 보기
        binding.uploadImage.setOnClickListener {
            val intent = Intent(this, FullScreenImage_Activity::class.java)
            intent.putExtra("imageUrl", gift.imageUrl)
/*            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)*/
            startActivity(intent)
            finish()
        }
    }

    var gift_Name = intent.getStringExtra("gift_Name")
    var gift_effectiveDate = intent.getStringExtra("gift_effectiveDate")
    var gift_barcode = intent.getStringExtra("gift_barcode")
    var gift_usage = intent.getStringExtra("gift_usage")
    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.collect_info_menu, menu)
        return true
    }

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
}
