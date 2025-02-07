package com.ssafy.firstproject.ui.mypageedit

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentProfileEditBinding


private const val TAG = "MyPageEditFragment"
class MyPageEditFragment : BaseFragment<FragmentProfileEditBinding>(
    FragmentProfileEditBinding::bind,
    R.layout.fragment_profile_edit
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivEditClose.setOnClickListener { findNavController().popBackStack() }}}