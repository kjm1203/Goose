package com.ssafy.firstproject.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentSearchBinding
import com.ssafy.firstproject.ui.home.adpater.NewsAdapter
import com.ssafy.firstproject.ui.search.viewmodel.SearchViewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>(
    FragmentSearchBinding::bind,
    R.layout.fragment_search
) {

    private lateinit var adapter: NewsAdapter
    private val viewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        initAdapter()
        observeNewsList()

        binding.etSearch.addTextChangedListener { keyword ->
            if (!keyword.isNullOrEmpty()) {
                viewModel.getSearchNewsList(keyword.toString())
            } else {
                adapter.submitList(emptyList())
            }
        }
    }

    private fun initAdapter() {
        adapter = NewsAdapter { id ->
            val action = SearchFragmentDirections.actionDestSearchToDestNewsDetail(id)
            findNavController().navigate(action)
        }
        binding.rvNews.adapter = adapter
    }

    private fun observeNewsList() {
        viewModel.newsList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}