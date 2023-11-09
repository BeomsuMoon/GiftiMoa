package com.example.giftimoa.bottom_nav_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.giftimoa.Login_activity
import com.example.giftimoa.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Menu_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Menu_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu_, container, false)

        // tv_logout 텍스트뷰를 찾아 클릭 이벤트를 추가
        val tvLogout = view.findViewById<TextView>(R.id.tv_logout)
        tvLogout.setOnClickListener {
            // 여기에 로그아웃 동작 및 페이지 전환을 추가
            // 예를 들어, LoginActivity로 이동하려면 다음과 같이 처리할 수 있습니다:
            val intent = Intent(activity, Login_activity::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Menu_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Menu_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}