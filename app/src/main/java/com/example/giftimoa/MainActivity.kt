package com.example.giftimoa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.giftimoa.bottom_nav_fragment.Collect_fragment
import com.example.giftimoa.bottom_nav_fragment.Chat_Fragment
import com.example.giftimoa.bottom_nav_fragment.Home_Fragment
import com.example.giftimoa.bottom_nav_fragment.Menu_Fragment
import com.example.giftimoa.bottom_nav_fragment.Search_Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val home_Fragment = Home_Fragment()
    private val collect_fragment = Collect_fragment()
    private val chat_Fragment = Chat_Fragment()
    private val menu_Fragment = Menu_Fragment()
    private var waitTime = 0L

    private val TAG = "SOL_LOG"

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {

        try {
            val information =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            val md = MessageDigest.getInstance("SHA")
            for (signature in signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashcode = String(Base64.encode(md.digest(), 0))
                Log.d("hashcode", "" + hashcode)
            }
        } catch (e: Exception) {
            Log.d("hashcode", "에러::" + e.toString())
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnavigationView)

        replaceFragment(home_Fragment)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)

            when (menuItem.itemId) {
                R.id.ic_home -> {
                    if (currentFragment is Home_Fragment) {
                        // Home_Fragment를 새 인스턴스로 교체하여 새로 고침
                        replaceFragment(Home_Fragment())
                    } else {
                        replaceFragment(home_Fragment)
                    }
                }
                R.id.ic_collect -> {
                    if (currentFragment is Collect_fragment) {
                        // Collect_fragment를 새 인스턴스로 교체하여 새로 고침
                        replaceFragment(Collect_fragment())
                    } else {
                        replaceFragment(collect_fragment)
                    }
                }
                R.id.ic_chat -> {
                    if (currentFragment is Chat_Fragment) {
                        // Chat_Fragment를 새 인스턴스로 교체하여 새로 고침
                        replaceFragment(Chat_Fragment())
                    } else {
                        replaceFragment(chat_Fragment)
                    }
                }
                R.id.ic_usermenu -> {
                    if (currentFragment is Menu_Fragment) {
                        // Menu_Fragment를 새 인스턴스로 교체하여 새로 고침
                        replaceFragment(Menu_Fragment())
                    } else {
                        replaceFragment(menu_Fragment)
                    }
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - waitTime >= 1500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish() // 액티비티 종료
        }
    }
}
