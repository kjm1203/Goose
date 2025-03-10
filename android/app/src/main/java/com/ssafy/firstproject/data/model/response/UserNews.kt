package com.ssafy.firstproject.data.model.response

data class UserNews(
    val analysisRequestedAt: List<Int?>,
    val analysisType: String,
    val biasScore: Double,
    val content: String,
    val description: String,
    val extractedAt: List<Int>,
    val id: String,
    val naverLink: String,
    val newsAgency: String,
    val originalLink: String,
    val paragraphReasons: List<String?>,
    val paragraphReliabilities: List<Double>,
    val paragraphs: List<String>,
    val pubDate: String,
    val reliability: Double,
    val title: String,
    val topImage: String,
    val username: String,
    val aiRate: Double,
    val evaluationMessage: String
)