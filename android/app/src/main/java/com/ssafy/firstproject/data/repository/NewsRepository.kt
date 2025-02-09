package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.NewsArticle
import com.ssafy.firstproject.data.source.remote.RetrofitUtil

class NewsRepository {

    private val newsService = RetrofitUtil.newsService

    suspend fun getNewsList(): List<NewsArticle> {
        return newsService.getNewsList()
    }

    suspend fun getSearchNewsList(keyword: String): List<NewsArticle> {
        return newsService.getSearchNewsList(keyword)
    }
}