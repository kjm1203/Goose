package com.ssafy.firstproject.ui.imageresult

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentImageResultBinding

private const val TAG = "ImageResultFragment"

class ImageResultFragment : BaseFragment<FragmentImageResultBinding>(
    FragmentImageResultBinding::bind,
    R.layout.fragment_image_result
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.arrowLeft.setOnClickListener {
            findNavController().popBackStack() // 이전 화면으로 이동
        }

        binding.btnOtherImgCheck.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

