package com.ssafy.firstproject.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsAnalysisArticle(
    val biasScore: Double?,
    val content: String?,
    val description: String?,
    val extractedAt: List<Int>?,
    val id: String = "",  // 기본값 설정
    val naverLink: String?,
    val newsAgency: String = "",
    val originalLink: String = "",
    val paragraphReasons: List<String?>,
    val paragraphReliabilities: List<Double?>,
    val paragraphs: List<String?>,
    val pubDate: String = "",
    val pubDateTimestamp: Long?,
    val reliability: Double?,
    val title: String = "",
    val topImage: String?
) : Parcelable