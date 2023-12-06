package com.example.giftimoa

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.giftimoa.ViewModel.ChatViewModel
import com.example.giftimoa.databinding.FragmentChatDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatDialogFragment : DialogFragment() {

    private var _binding: FragmentChatDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatViewModel: ChatViewModel

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
        chatViewModel = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)

        binding.cancelButton.setOnClickListener{dismiss()}
        val re_nickname = arguments?.getString("renickname")
        val re_brand = arguments?.getString("brand")
        val myNickname = arguments?.getString("myNickname")

        binding.editTextReciverNickname.setText(re_nickname)
        binding.editTextMyNickname.setText(myNickname)
        binding.editTextBrand.setText(re_brand)

        binding.buttonSend.setOnClickListener {
            val renickname = binding.editTextReciverNickname.text.toString()
            val mynickname = binding.editTextMyNickname.text.toString()
            val brand = binding.editTextBrand.text.toString()
            val message = binding.editTextMessage.text.toString()

            // null 또는 빈 문자열이 있는지 확인
            if (renickname.isBlank() || mynickname.isBlank() || brand.isBlank() || message.isBlank()) {
                Toast.makeText(requireContext(), "입력 필드를 모두 채워주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val timestamp = dateFormat.format(currentTime)

            try {
                chatViewModel.sendMessage(mynickname, renickname, brand, message, timestamp)
                dismiss() // 다이얼로그 닫기
                Toast.makeText(
                    requireContext(),
                    "${renickname}님에게 메시지가 전송되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                // Handle exceptions here
                Log.e("ChatDialogFragment", "Error sending message: ${e.message}")
                // Show error message to the user, if needed
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(bundle: Bundle): ChatDialogFragment {
            val fragment = ChatDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
