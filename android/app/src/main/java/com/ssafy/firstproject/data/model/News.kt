package com.ssafy.firstproject.data.model

data class News(
    val content: String,
    val description: String,
    val extractedAt: List<Int>,
    val id: String,
    val naverLink: String,
    val originalLink: String,
    val pubDate: String,
    val pubDateTimestamp: Long,
    val title: String,
    val topImage: String
)