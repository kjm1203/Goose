package com.ssafy.firstproject.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsAnalysisArticle(
    val biasScore: Double?,  // JSON의 실수형 biasScore 반영
    val content: String?,  // null 가능하도록 변경
    val description: String?,
    val extractedAt: List<Int>?,  // JSON의 배열 형식 반영
    val id: String,
    val naverLink: String?,
    val newsAgency: String,
    val originalLink: String,
    val paragraphReasons: List<String?>,
    val paragraphReliabilities: List<Double?>,  // JSON의 1.0 → Double로 변경
    val paragraphs: List<String?>,
    val pubDate: String,
    val pubDateTimestamp: Long?,  // Int → Long으로 변경
    val reliability: Double?,  // JSON에서 Double 타입으로 들어오므로 수정
    val title: String,
    val topImage: String?
) : Parcelable