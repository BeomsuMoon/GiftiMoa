package com.example.giftimoa

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.databinding.LayoutCollectGiftEditBinding
import com.example.giftimoa.dto.Collect_Gift
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Collect_gift_edit_activity : AppCompatActivity() {
    private lateinit var binding: LayoutCollectGiftEditBinding
    private lateinit var gift: Collect_Gift
    private lateinit var giftViewModel: Gificon_ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutCollectGiftEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftViewModel = ViewModelProvider(this).get(Gificon_ViewModel::class.java)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 정보 수정"

        binding.editEffectiveDate.setOnClickListener {
            showDatePickerDialog()
        }

        gift = intent.getSerializableExtra("gift") as Collect_Gift

        binding.apply {
            editGiftName.setText(gift.giftName)
            editEffectiveDate.setText(gift.effectiveDate)
            editBarcode.setText(gift.barcode)
            editUsage.setText(gift.usage)

            Glide.with(this@Collect_gift_edit_activity)
                .load(gift.imageUrl)
                .into(uploadImage)
        }

        binding.btnSave.setOnClickListener {
            // 입력 필드에서 수정된 정보 가져오기
            val newGiftName = binding.editGiftName.text.toString()
            val newEffectiveDate = binding.editEffectiveDate.text.toString()
            val newBarcode = binding.editBarcode.text.toString()
            val newUsage = binding.editUsage.text.toString()

            // 새로운 Collect_Gift 객체 생성
            val updatedGift = Collect_Gift(
                ID = gift.ID,
                giftName = newGiftName,
                effectiveDate = newEffectiveDate,
                barcode = newBarcode,
                usage = newUsage,
                imageUrl = gift.imageUrl,
                state = gift.state
            )

            Log.d("로그", "수정 된 기프티콘 : $updatedGift")

            // ViewModel에 Collect_Gift 객체 업데이트
            giftViewModel.updateGift(updatedGift)

            // 수정된 결과를 액티비티에 전달
            val intent = Intent()
            intent.putExtra("modifiedGift", updatedGift)
            setResult(Activity.RESULT_OK, intent)

            // 이전 화면으로 돌아가기
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            binding.editEffectiveDate.setText(date)
        }, year, month, day).show()
    }
}
