package com.ssafy.firstproject.data.model

data class NewsAnalysisArticle(
    val content: String,
    val description: String,
    val extractedAt: List<Int>,
    val naverLink: String,
    val originalLink: String,
    val paragraphs: String,
    val pubDate: String,
    val title: String,
    val topImage: String
)