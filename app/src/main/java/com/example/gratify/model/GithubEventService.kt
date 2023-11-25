package com.example.gratify.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubEventService {
    @GET("users/{username}/events")
    fun getUserEvents(@Path("username") username: String): Call<List<GithubEventResponse>>
}