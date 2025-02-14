package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.response.AuthResponse
import com.ssafy.firstproject.data.model.response.DuplicateCheckResponse
import com.ssafy.firstproject.data.model.request.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("users/signup")
    suspend fun signUp(@Body user: User): Response<AuthResponse>

    @POST("users/login")
    suspend fun login(@Body user: User): Response<AuthResponse>

    @POST("users/logout")
    suspend fun logout(@Header("Authorization") accessToken: String): Response<AuthResponse>

    @GET("users/check-username")
    suspend fun checkUserName(@Query("username") id: String): Response<DuplicateCheckResponse>

    @GET("users/check-nickname")
    suspend fun checkNIckName(@Query("nickname") nickname: String): Response<DuplicateCheckResponse>
}