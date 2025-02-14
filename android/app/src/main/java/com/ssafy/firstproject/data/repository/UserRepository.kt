package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.AuthResponse
import com.ssafy.firstproject.data.model.DuplicateCheckResponse
import com.ssafy.firstproject.data.model.User
import com.ssafy.firstproject.data.source.remote.RetrofitUtil
import retrofit2.Response

class UserRepository {

    private val userService = RetrofitUtil.userService

    suspend fun signUp(user: User): Response<AuthResponse> {
        return userService.signUp(user)
    }

    suspend fun login(user: User): Response<AuthResponse> {
        return userService.login(user)
    }

    suspend fun logout(accessToken: String): Response<AuthResponse> {
        return userService.logout(accessToken)
    }

    suspend fun checkId(id: String): Response<DuplicateCheckResponse> {
        return userService.checkUserName(id)
    }

    suspend fun checkNickname(nickname: String): Response<DuplicateCheckResponse> {
        return userService.checkNIckName(nickname)
    }

}