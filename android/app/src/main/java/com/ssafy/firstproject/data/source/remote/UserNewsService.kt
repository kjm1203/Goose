package com.ssafy.firstproject.data.source.remote

import com.ssafy.firstproject.data.model.UserNews
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserNewsService {

    @GET("users/{username}/determinations")
    suspend fun getUserNewsList(
        @Path("username") username: String,
    ) : Response<List<UserNews>>

}

