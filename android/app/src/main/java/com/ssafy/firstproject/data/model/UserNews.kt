package com.ssafy.firstproject.data.model

data class UserNews(
    val determinedAt: Int,
    val newsContent: String,
    val newsId: String,
    val newsTitle: String,
    val reliability: Int,
    val searchType: String
)

// 판별한 뉴스의 이미지?는 안 오나? 아 이것도 URL이면 뉴스의 이미지이지만
// 나머지 이미지, 텍스트는 어떻게 하기로 했더라?
// 이거 원래 뉴스의 형식이랑 다르게 저장이 되는건가?
