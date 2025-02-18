package com.ssafy.firstproject.data.model.request

data class GameResultRequest(
    val dwellTime: Long,
    val newsId: String,
    val userChoice: String
)