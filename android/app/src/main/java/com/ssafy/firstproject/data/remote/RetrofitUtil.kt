package com.ssafy.firstproject.data.remote

import com.ssafy.firstproject.base.ApplicationClass

class RetrofitUtil {
    companion object {
        val userService: UserService = ApplicationClass.retrofit.create(UserService::class.java)
        val newsService: NewsService = ApplicationClass.retrofit.create(NewsService::class.java)
    }
}