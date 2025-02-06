package com.ssafy.firstproject.ui.login

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind,
    R.layout.fragment_login
){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.dest_signup)
        }
    }
}