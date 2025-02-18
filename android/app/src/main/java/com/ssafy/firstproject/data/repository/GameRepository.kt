package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.request.UserChoiceRequest
import com.ssafy.firstproject.data.model.response.FakeNews
import com.ssafy.firstproject.data.model.response.GameStatistics
import com.ssafy.firstproject.data.source.remote.RetrofitUtil
import retrofit2.Response

class GameRepository {

    private val gameService = RetrofitUtil.gameService

    suspend fun getFakeNews(): Response<FakeNews> {
        return gameService.getFakeNews()
    }

    suspend fun submitFakeNewsResult(userChoiceRequest: UserChoiceRequest): Response<Unit> {
        return gameService.submitFakeNewsResult(userChoiceRequest)
    }

    suspend fun getGameStatistics(newsId: String): Response<GameStatistics> {
        return gameService.getGameStatistics(newsId)
    }
}