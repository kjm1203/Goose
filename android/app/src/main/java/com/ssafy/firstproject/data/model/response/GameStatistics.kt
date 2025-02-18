package com.ssafy.firstproject.data.model.response

data class GameStatistics(
    val correctAnswer: String,
    val fakeReason: String,
    val selectionPercentages: Map<String, Double>
)