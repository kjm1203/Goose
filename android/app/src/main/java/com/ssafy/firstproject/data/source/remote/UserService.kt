package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.AuthResponse
import com.ssafy.firstproject.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {

    @POST("users/signup")
    suspend fun signUp(@Body user: User): Response<AuthResponse>

    @POST("users/login")
    suspend fun login(@Body user: User): Response<AuthResponse>

    @POST("users/logout")
    suspend fun logout(@Header("Authorization") accessToken: String): Response<AuthResponse>

}