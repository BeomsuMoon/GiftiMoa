package com.example.giftimoa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.giftimoa.ViewModel.Gifticon_ViewModel
import com.example.giftimoa.bottom_nav_fragment.Collect_fragment
import com.example.giftimoa.databinding.LayoutCollectGiftAddBinding
import com.example.giftimoa.dto.Collect_Gift
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.InputStream

class Collect_gift_add_activity : AppCompatActivity() {
    private lateinit var binding : LayoutCollectGiftAddBinding
    val viewModel = ViewModelProvider(this).get(Gifticon_ViewModel::class.java)


    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    private var giftList = mutableListOf<Collect_Gift>()

    lateinit var tess: TessBaseAPI //Tesseract API 객체 생성
    var dataPath: String = "" //데이터 경로 변수 선언

    var dataUri: Uri? = null
    var str_uri: String = ""
    var origin_uri:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutCollectGiftAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        str_uri=intent.getStringExtra("intent_uri").toString()
        origin_uri = str_uri

        //액션바 활성화
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "기프티콘 등록"

        galleryClickEvent()
        giftAdd_Btn()

    }

    //이미지 클릭시 갤러리 권한 요청 및
    private fun galleryClickEvent(){
        binding.uploadImage.setOnClickListener ({
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                //권한이 허용되지 않음
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ){
                    //이전에 이미 권한이 거부되었을 때 설명
                    var dlg = AlertDialog.Builder(this)
                    dlg.setTitle("권한이 필요한 이유")
                    dlg.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                    dlg.setPositiveButton("확인") { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@Collect_gift_add_activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    dlg.setNegativeButton("취소", null)
                    dlg.show()
                }else {
                    //권한 요청
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            } else {

                loadImage()
/*
                dataPath = filesDir.toString() + "/tesseract/" //언어데이터의 경로 미리 지정

                checkFile(File(dataPath + "tessdata/"), "kor") //사용할 언어파일의 이름 지정
                checkFile(File(dataPath + "tessdata/"), "eng")

                var lang: String = "kor+eng"
                tess = TessBaseAPI() //api준비
                Log.d("sys", "api")
                tess.setDebug(true)
                Log.d("sys", "디버그")
                tess.init(dataPath, lang) //해당 사용할 언어데이터로 초기화
                Log.d("sys", "언어초기화")
*/

            }
        })
    }
    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResult.launch(intent)
    }
    //갤러리 호출
    private val activityResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        if(it.resultCode == RESULT_OK && it.data != null){
            val uri = it.data!!.data

            Glide.with(this)
                .load(uri)
                .into(binding.uploadImage)
        }
    }

    override fun onSupportNavigateUp(): Boolean { // 액션바 뒤로가기
        onBackPressed()
        return true
    }


    private fun giftAdd() {
            var giftName = binding.textGiftName.text.toString()
            var effectiveDate = binding.textEffectiveDate.text.toString()
            var barcode = binding.textBarcode.text.toString()
            var usage = binding.textUsage.text.toString()

        viewModel.setGiftData(giftName, effectiveDate, barcode, usage)

            val intent = Intent(this, Collect_gift_add_info_activity::class.java)
            intent.putExtra("gift_Name", giftName)
            intent.putExtra("gift_effectiveDate", effectiveDate)
            intent.putExtra("gift_barcode", barcode)
            intent.putExtra("gift_usage", usage)
            startActivity(intent)

            //텍스트 필드 초기화
            binding.textGiftName.text.clear()
            binding.textEffectiveDate.text.clear()
            binding.textBarcode.text.clear()
            binding.textUsage.text.clear()
    }
    private fun giftAdd_Btn() {
        binding.addBtn.setOnClickListener {
            giftAdd()
        }
    }
}