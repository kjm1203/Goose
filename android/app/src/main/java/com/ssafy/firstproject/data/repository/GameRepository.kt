package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.response.FakeNews
import com.ssafy.firstproject.data.source.remote.RetrofitUtil
import retrofit2.Response

class GameRepository {

    private val gameService = RetrofitUtil.gameService

    suspend fun getFakeNews(): Response<FakeNews> {
        return gameService.getFakeNews()
    }
}