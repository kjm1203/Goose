package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.News
import retrofit2.http.GET

interface NewsService {

    @GET("news/list")
    suspend fun getNewsList(): List<News>
}