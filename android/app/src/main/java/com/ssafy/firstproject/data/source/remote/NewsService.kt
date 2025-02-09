package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.NewsArticle
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("news/list")
    suspend fun getNewsList(): List<NewsArticle>

    @GET("news/search")
    suspend fun getSearchNewsList(
        @Query("keyword") keyword: String
    ): List<NewsArticle>
}