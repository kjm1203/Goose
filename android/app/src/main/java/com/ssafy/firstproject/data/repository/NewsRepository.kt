package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.NewsArticle
import com.ssafy.firstproject.data.source.remote.RetrofitUtil
import retrofit2.Response

class NewsRepository {

    private val newsService = RetrofitUtil.newsService

    suspend fun getNewsList(): Response<List<NewsArticle>> {
        return newsService.getNewsList()
    }

    suspend fun getSearchNewsList(keyword: String): Response<List<NewsArticle>> {
        return newsService.getSearchNewsList(keyword)
    }
}