package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.request.GameResultRequest
import com.ssafy.firstproject.data.model.response.FakeNews
import com.ssafy.firstproject.data.model.response.GameStatistics
import com.ssafy.firstproject.data.source.remote.RetrofitUtil
import retrofit2.Response

class GameRepository {

    private val gameService = RetrofitUtil.gameService

    suspend fun getFakeNews(): Response<FakeNews> {
        return gameService.getFakeNews()
    }

    suspend fun submitFakeNewsResult(gameResultRequest: GameResultRequest): Response<Unit> {
        return gameService.submitFakeNewsResult(gameResultRequest)
    }

    suspend fun getGameStatistics(newsId: String): Response<GameStatistics> {
        return gameService.getGameStatistics(newsId)
    }
}