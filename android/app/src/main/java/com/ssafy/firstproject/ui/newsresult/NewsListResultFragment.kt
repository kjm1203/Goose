package com.ssafy.firstproject.ui.newsresult

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentNewsListResultBinding
import com.ssafy.firstproject.ui.home.HomeFragmentDirections
import com.ssafy.firstproject.ui.home.adpater.NewsAdapter

class NewsListResultFragment : BaseFragment<FragmentNewsListResultBinding>(
    FragmentNewsListResultBinding::bind,
    R.layout.fragment_news_list_result
) {

    private lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarNewsResult.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        initAdapter()
    }

    private fun initAdapter() {
        adapter = NewsAdapter { id ->
            val action = HomeFragmentDirections.actionDestHomeToDestNewsDetail(id)
            findNavController().navigate(action)
        }

        binding.rvNewsResult.adapter = adapter
    }
}