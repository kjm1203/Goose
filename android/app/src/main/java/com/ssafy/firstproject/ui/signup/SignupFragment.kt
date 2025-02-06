package com.ssafy.firstproject.ui.signup

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentSignupBinding

class SignupFragment : BaseFragment<FragmentSignupBinding>(
    FragmentSignupBinding::bind,
    R.layout.fragment_signup
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSignupClose.setOnClickListener { findNavController().popBackStack() }
    }
}