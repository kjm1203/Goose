package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.request.UserChoiceRequest
import com.ssafy.firstproject.data.model.response.FakeNews
import com.ssafy.firstproject.data.model.response.GameStatistics
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GameService {

    @GET("fake-news/random")
    suspend fun getFakeNews(): Response<FakeNews>

    @POST("game-result/submit")
    suspend fun submitFakeNewsResult(
        @Body userChoiceRequest: UserChoiceRequest
    ): Response<Unit>

    @GET("statistics/news")
    suspend fun getGameStatistics(
        @Query("newsId") newsId: String
    ): Response<GameStatistics>
}