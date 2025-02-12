package com.ssafy.firstproject.ui.signup

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.data.model.User
import com.ssafy.firstproject.databinding.FragmentSignupBinding
import com.ssafy.firstproject.ui.signup.viewmodel.SignupViewModel

class SignupFragment : BaseFragment<FragmentSignupBinding>(
    FragmentSignupBinding::bind,
    R.layout.fragment_signup
) {

    private var isIdVerified = false
    private var isPasswordVerified = false
    private var isPasswordSame = false
    private var isNicknameVerified = false

    private val viewModel by viewModels<SignupViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSignUpSuccess()
        initEvent()
    }

    private fun initEvent() {

        binding.ivSignupClose.setOnClickListener { findNavController().popBackStack() }

        checkAllVerified()

        binding.tieSignupIdInput.addTextChangedListener {
            isIdVerified = validateInput(binding.tieSignupIdInput, binding.tvIdInputMetaInfo)
            checkAllVerified()
        }

        binding.tieSignupPwInput.addTextChangedListener {
            isPasswordVerified = validateInput(binding.tieSignupPwInput, binding.tvPwMetaInfo)
            checkAllVerified()
        }

        binding.tieSignupNicknameInput.addTextChangedListener {
            isNicknameVerified =
                validateInput(binding.tieSignupNicknameInput, binding.tvNicknameMetaInfo)
            checkAllVerified()
        }

        binding.tieSignupPwCheckInput.addTextChangedListener {
            isPasswordSame = validatePassword(
                passwordEditText = binding.tieSignupPwInput,
                confirmPasswordEditText = binding.tieSignupPwCheckInput,
                metaTextView = binding.tvPwMetaInfo
            )
            checkAllVerified()
        }

        binding.btnSignup.setOnClickListener {
            viewModel.signUp(
                User(
                    username = binding.tieSignupIdInput.text.toString(),
                    password = binding.tieSignupPwInput.text.toString(),
                    nickname = binding.tieSignupNicknameInput.text.toString()
                )
            )
        }
    }

    private fun checkAllVerified() {
        binding.btnSignup.isEnabled =
            isIdVerified && isPasswordVerified && isPasswordSame && isNicknameVerified
    }

    private fun validateInput(editText: TextInputEditText, textView: TextView): Boolean {
        val text = editText.text.toString().trim()
        val pattern = "^[a-z0-9]{4,12}$".toRegex()

        when {
            text.isEmpty() -> {
                textView.visibility = View.GONE
                return false
            }

            !pattern.matches(text) -> {
                textView.visibility = View.VISIBLE
                textView.text = getString(R.string.user_limit_message)
                return false
            }

            else -> {
                textView.visibility = View.GONE
                return true
            }
        }
    }

    private fun validatePassword(
        passwordEditText: TextInputEditText,
        confirmPasswordEditText: TextInputEditText,
        metaTextView: TextView
    ): Boolean {
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        when {
            password.isEmpty() || confirmPassword.isEmpty() -> {
                metaTextView.visibility = View.GONE
                return false
            }

            password != confirmPassword -> {
                metaTextView.visibility = View.VISIBLE
                metaTextView.text = getString(R.string.user_not_correct_message)
                return false
            }

            else -> {
                metaTextView.visibility = View.GONE
                return true
            }
        }
    }

    private fun observeSignUpSuccess() {
        viewModel.isSignupSuccess.observe(viewLifecycleOwner) {
            if (it) {
                showToast("회원 가입에 성공하였습니다")
                findNavController().navigate(R.id.action_dest_signup_to_dest_login)
            } else {
                showToast("회원 가입에 실패하였습니다. 다시 시도해주세요.")
            }
        }
    }
}