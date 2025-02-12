package com.ssafy.firstproject.ui.newsresult

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentNewsResultBinding

class NewsResultFragment : BaseFragment<FragmentNewsResultBinding>(
    FragmentNewsResultBinding::bind,
    R.layout.fragment_news_result
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarNewsResult.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}