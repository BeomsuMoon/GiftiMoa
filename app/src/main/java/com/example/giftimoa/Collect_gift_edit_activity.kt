package com.example.giftimoa

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gificon_ViewModel
import com.example.giftimoa.adpater_list.UpdateAndRefreshAdapter
import com.example.giftimoa.databinding.LayoutCollectGiftEditBinding
import com.example.giftimoa.dto.Collect_Gift
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
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

            // ViewModel에 Collect_Gift 객체 업데이트
            giftViewModel.updateGift(updatedGift)

            // 수정된 결과를 액티비티에 전달
            val intent = Intent()
            intent.putExtra("modifiedGift", updatedGift)
            setResult(Activity.RESULT_OK, intent)

            // UpdateAndRefreshAdapter 초기화
            val adapter = UpdateAndRefreshAdapter(mutableListOf(updatedGift)) { _ ->

                Toast.makeText(this@Collect_gift_edit_activity, "기프티콘 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
            }
            lifecycleScope.launch {
                Log.d("kimjyeongki" , "$updatedGift")
                updateGiftOnServer(updatedGift)
            }

            adapter.updateGift(updatedGift)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private suspend fun updateGiftOnServer(updatedGift: Collect_Gift) {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                // 서버에 보낼 JSON 데이터 생성
                val json = """
                {
                    "gift_name": "${updatedGift.giftName}",
                    "effective_date": "${updatedGift.effectiveDate}",
                    "barcode": "${updatedGift.barcode}",
                    "usage_description": "${updatedGift.usage}",
                    "image": "${updatedGift.imageUrl}",
                    "state": ${updatedGift.state}
                }
            """.trimIndent()

                // 서버 업데이트 요청
                val requestBody = json.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("http://3.35.110.246:3306/update_gift/${updatedGift.ID}")
                    .put(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                Log.d("kimjyeongki1", "${updatedGift.ID}")

                if (response.isSuccessful) {
                    // 서버 업데이트 성공
                    // 원하는 작업 수행
                } else {
                    // 서버 업데이트 실패
                    // 원하는 작업 수행
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // 예외 처리
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            // 현재 날짜와 선택한 날짜를 비교
            if (selectedDate.before(calendar)) {
                // 선택한 날짜가 현재 날짜보다 이전이면 다이얼로그를 표시하고 effectiveDate를 비웁니다.
                val builder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.dialog_gifticon_date_cancle, null)
                builder.setView(dialogLayout)
                val dialog = builder.create()
                dialog.setOnShowListener {
                    val okButton = dialog.findViewById<TextView>(R.id.btn_ok)
                    okButton?.setOnClickListener {
                        dialog.dismiss()
                        binding.editEffectiveDate.setText("")
                    }
                }
                dialog.show()
            } else {
                // 선택한 날짜가 현재 날짜보다 이후이면 effectiveDate를 설정합니다.
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                binding.editEffectiveDate.setText(date)
            }
        }, year, month, day).show()
    }
}