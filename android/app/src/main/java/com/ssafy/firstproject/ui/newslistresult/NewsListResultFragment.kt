package com.ssafy.firstproject.ui.newslistresult

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentNewsListResultBinding
import com.ssafy.firstproject.ui.home.adpater.NewsAdapter

private const val TAG = "NewsListResultFragment_ssafy"
class NewsListResultFragment : BaseFragment<FragmentNewsListResultBinding>(
    FragmentNewsListResultBinding::bind,
    R.layout.fragment_news_list_result
) {
    private val args: NewsListResultFragmentArgs by navArgs()

    private lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsText = args.newsText

        Log.d(TAG, "onViewCreated: $newsText")

        binding.toolbarNewsListResult.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        initAdapter()
    }

    private fun initAdapter() {
        adapter = NewsAdapter { id -> }

        binding.rvNewsResult.adapter = adapter
    }
}