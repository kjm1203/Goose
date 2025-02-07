package com.ssafy.firstproject.ui.signup

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentSignupBinding

private const val TAG = "SignupFragment_ssafy"
class SignupFragment : BaseFragment<FragmentSignupBinding>(
    FragmentSignupBinding::bind,
    R.layout.fragment_signup
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSignupClose.setOnClickListener { findNavController().popBackStack() }

        binding.tieSignupIdInput.addTextChangedListener {
            validateInput(binding.tieSignupIdInput, binding.tvIdInputMetaInfo)
        }

        binding.tieSignupPwInput.addTextChangedListener {
            validateInput(binding.tieSignupPwInput, binding.tvPwMetaInfo)
        }

        binding.tieSignupNicknameInput.addTextChangedListener {
            validateInput(binding.tieSignupNicknameInput, binding.tvNicknameMetaInfo)
        }
    }

    private fun validateInput(editText: TextInputEditText, textView: TextView) {
        val text = editText.text.toString().trim()
        val pattern = "^[a-z0-9]{4,12}$".toRegex()

        when {
            text.isEmpty() -> { textView.visibility = View.GONE }
            !pattern.matches(text) -> { textView.visibility = View.VISIBLE }
            else -> { textView.visibility = View.GONE }
        }
    }
}