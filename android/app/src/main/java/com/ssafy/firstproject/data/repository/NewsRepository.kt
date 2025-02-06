package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.News
import com.ssafy.firstproject.data.source.remote.RetrofitUtil

class NewsRepository {

    private val newsService = RetrofitUtil.newsService

    suspend fun getNewsList(): List<News> {
        return newsService.getNewsList()
    }
}