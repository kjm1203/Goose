package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.UserNews
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserNewsService {

    @GET("content/my-news-analysis")
    suspend fun getUserNewsList() : Response<List<UserNews>>

}

