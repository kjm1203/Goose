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

class SignupFragment : BaseFragment<FragmentSignupBinding>(
    FragmentSignupBinding::bind,
    R.layout.fragment_signup
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
    }

    private fun initEvent() {
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

        binding.tieSignupPwCheckInput.addTextChangedListener {
            validatePassword(
                passwordEditText = binding.tieSignupPwInput,
                confirmPasswordEditText = binding.tieSignupPwCheckInput,
                metaTextView = binding.tvPwMetaInfo
            )
        }
    }

    private fun validateInput(editText: TextInputEditText, textView: TextView) {
        val text = editText.text.toString().trim()
        val pattern = "^[a-z0-9]{4,12}$".toRegex()

        when {
            text.isEmpty() -> { textView.visibility = View.GONE }
            !pattern.matches(text) -> {
                textView.visibility = View.VISIBLE
                textView.text = getString(R.string.user_limit_message)
            }
            else -> { textView.visibility = View.GONE }
        }
    }

    private fun validatePassword(
        passwordEditText: TextInputEditText,
        confirmPasswordEditText: TextInputEditText,
        metaTextView: TextView) {
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        when {
            password.isEmpty() || confirmPassword.isEmpty() -> { metaTextView.visibility = View.GONE }
            password != confirmPassword -> {
                metaTextView.visibility = View.VISIBLE
                metaTextView.text = getString(R.string.user_not_correct_message)
            }
            else -> { metaTextView.visibility = View.GONE }
        }
    }
}