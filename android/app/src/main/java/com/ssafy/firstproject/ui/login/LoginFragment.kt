package com.ssafy.firstproject.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.data.model.User
import com.ssafy.firstproject.databinding.FragmentLoginBinding
import com.ssafy.firstproject.ui.login.viewmodel.LoginViewModel
import com.ssafy.firstproject.util.setOnSingleClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind,
    R.layout.fragment_login
) {

    val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.dest_signup)
        }

        binding.btnLogin.setOnSingleClickListener {
            if (
                binding.tieLoginIdInput.text.toString().isEmpty()
                || binding.tieLoginPwInput.text.toString().isEmpty()
            ) {
                showToast("아이디와 비밀번호를 입력해주세요.😊")
                return@setOnSingleClickListener
            }
            viewModel.login(
                User(
                    username = binding.tieLoginIdInput.text.toString(),
                    password = binding.tieLoginPwInput.text.toString()
                )
            )
        }

        observeLogin()
    }

    private fun observeLogin() {
        viewModel.isLoginSuccess.observe(viewLifecycleOwner) {
            if (it) {
                showToast("로그인에 성공하였습니다.😊")
                findNavController().navigate(
                    R.id.dest_home,  // 이동할 목적지
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true) // 네비게이션 그래프의 루트까지 제거
                        .build()
                )
            } else {
                showToast("로그인에 실패하였습니다. 다시 시도해주세요.😅")
            }
        }
    }

}