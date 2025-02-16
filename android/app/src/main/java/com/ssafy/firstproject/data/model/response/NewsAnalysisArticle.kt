package com.ssafy.firstproject.data.model.response

data class NewsAnalysisArticle(
    val biasScore: Int,
    val content: String,
    val description: String,
    val extractedAt: String,
    val id: String,
    val naverLink: String,
    val newsAgency: String,
    val originalLink: String,
    val paragraphReasons: List<String>,
    val paragraphReliabilities: List<Int>,
    val paragraphs: List<String>,
    val pubDate: String,
    val pubDateTimestamp: Int,
    val reliability: Int,
    val title: String,
    val topImage: String
)