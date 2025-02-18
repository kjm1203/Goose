package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.response.FakeNews
import com.ssafy.firstproject.data.model.response.GameStatistics
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GameService {

    @GET("fake-news/random")
    suspend fun getFakeNews(): Response<FakeNews>

    @GET("statistics/news")
    suspend fun getGameStatistics(
        @Query("newsId") newsId: String
    ): Response<GameStatistics>
}