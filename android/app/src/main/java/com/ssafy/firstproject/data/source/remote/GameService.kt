package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.response.FakeNews
import retrofit2.Response
import retrofit2.http.GET

interface GameService {

    @GET("fake-news/random")
    suspend fun getFakeNews(): Response<FakeNews>
}