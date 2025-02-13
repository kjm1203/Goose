package com.ssafy.firstproject.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.data.model.User
import com.ssafy.firstproject.databinding.FragmentSignupBinding
import com.ssafy.firstproject.ui.signup.viewmodel.SignupViewModel

class SignupFragment : BaseFragment<FragmentSignupBinding>(
    FragmentSignupBinding::bind,
    R.layout.fragment_signup
) {

    private val viewModel by viewModels<SignupViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSignUpSuccess()
        initEvent()
    }

    private fun initEvent() {

        binding.ivSignupClose.setOnClickListener { findNavController().popBackStack() }

        binding.btnSignup.setOnClickListener {
            viewModel.signUp(
                User(
                    username = binding.tieSignupIdInput.text.toString(),
                    password = binding.tieSignupPwInput.text.toString(),
                    nickname = binding.tieSignupNicknameInput.text.toString()
                )
            )
        }

        observeValidation()

        binding.tieSignupIdInput.addTextChangedListener {
            viewModel.checkIdValidation(it.toString())
            viewModel.checkAllValidation()
        }

        binding.tieSignupPwInput.addTextChangedListener {
            viewModel.checkPasswordValidation(it.toString())
            viewModel.checkAllValidation()
        }

        binding.tieSignupPwCheckInput.addTextChangedListener {
            viewModel.checkPasswordMatch(
                binding.tieSignupPwInput.text.toString(),
                it.toString()
            )
            viewModel.checkAllValidation()
        }

        binding.tieSignupNicknameInput.addTextChangedListener {
            viewModel.checkNickNameValidation(it.toString())
            viewModel.checkAllValidation()
        }
    }

    private fun observeValidation() {

        viewModel.isIdValid.observe(viewLifecycleOwner) {
            if (!it) {
                binding.tvIdInputMetaInfo.visibility = View.VISIBLE
                binding.tvIdInputMetaInfo.text = getString(R.string.user_limit_message)
            } else {
                binding.tvIdInputMetaInfo.visibility = View.GONE
            }
        }

        viewModel.isPasswordValid.observe(viewLifecycleOwner) {
            if (!it) {
                binding.tvPwMetaInfo.visibility = View.VISIBLE
                binding.tvPwMetaInfo.text = getString(R.string.user_limit_message)
            } else {
                binding.tvPwMetaInfo.visibility = View.GONE
            }
        }

        viewModel.isPasswordMatch.observe(viewLifecycleOwner) {
            if (!it) {
                binding.tvPwMetaInfo.visibility = View.VISIBLE
                binding.tvPwMetaInfo.text = getString(R.string.user_not_correct_message)
                binding.tvPwMetaInfo.setTextColor(getColor(requireContext(), R.color.maximumRed))
            } else {
                if (viewModel.isPasswordValid.value == true) {
                    binding.tvPwMetaInfo.text = getString(R.string.user_password_message)
                    binding.tvPwMetaInfo.setTextColor(Color.BLUE)
                }
            }
        }

        viewModel.isNicknameValid.observe(viewLifecycleOwner) {
            if (!it) {
                binding.tvNicknameMetaInfo.visibility = View.VISIBLE
                binding.tvNicknameMetaInfo.text = getString(R.string.nickname_limit_message)
            } else {
                binding.tvNicknameMetaInfo.visibility = View.GONE
            }
        }

        viewModel.isAllValid.observe(viewLifecycleOwner) {
            binding.btnSignup.isEnabled = it
        }
    }

    private fun observeSignUpSuccess() {
        viewModel.isSignupSuccess.observe(viewLifecycleOwner) {
            if (it) {
                showToast("회원 가입에 성공하였습니다.😊")
                findNavController().navigate(R.id.action_dest_signup_to_dest_login)
            } else {
                showToast("회원 가입에 실패하였습니다. 다시 시도해주세요.😅")
            }
        }
    }
}