package com.safeguardFamily.diabezone.apiService

import com.safeguardFamily.diabezone.model.request.UserResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("posts")
    fun getUsers() : Call<UserResponse>

}