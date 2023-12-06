package com.example.giftimoa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            val currentTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val renickname = binding.editTextReciverNickname.text.toString()
            val mynickname = binding.editTextMyNickname.text.toString()
            val brand = binding.editTextBrand.text.toString()
            val message = binding.editTextMessage.text.toString()
            val timestamp = dateFormat.format(currentTime)

            try {
                chatViewModel.sendMessage(mynickname, renickname, brand, message, timestamp)
                dismiss() // 다이얼로그 닫기
            } catch (e: Exception) {
                // Handle exceptions here
                Log.e("ChatDialogFragment", "Error sending message: ${e.message}")
                // Show error message to the user, if needed
            }
            dismiss() // 다이얼로그 닫기
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
