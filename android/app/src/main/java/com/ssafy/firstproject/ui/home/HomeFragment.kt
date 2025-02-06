package com.ssafy.firstproject.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentHomeBinding
import com.ssafy.firstproject.ui.home.adpater.NewsAdapter
import com.ssafy.firstproject.ui.home.viewmodel.HomeViewModel

private const val TAG = "HomeFragment"

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private lateinit var adapter: NewsAdapter
    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        observeNewsList()
    }

    private fun initAdapter() {
        adapter = NewsAdapter {}
        binding.rvNews.adapter = adapter
    }

    private fun observeNewsList() {
        viewModel.newsList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}