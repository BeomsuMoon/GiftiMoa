package com.example.giftimoa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.giftimoa.databinding.FragmentChatDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatDialogFragment : DialogFragment() {

    private var _binding: FragmentChatDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cancelButton.setOnClickListener{dismiss()}

        val re_nickname = arguments?.getString("renickname")
        val re_brand = arguments?.getString("brand")
        val myNickname = arguments?.getString("myNickname")

        binding.editTextReciverNickname.setText(re_nickname)
        binding.editTextMyNickname.setText(myNickname)
        binding.editTextBrand.setText(re_brand)


        binding.buttonSend.setOnClickListener {
            val currentTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val renickname = binding.editTextReciverNickname.text.toString()
            val mynickname = binding.editTextMyNickname.text.toString()
            val brand = binding.editTextBrand.text.toString()
            val message = binding.editTextMessage.text.toString()
            val timestamp = dateFormat.format(currentTime)


            dismiss() // 다이얼로그 닫기
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
