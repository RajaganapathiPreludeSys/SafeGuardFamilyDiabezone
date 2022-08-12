package com.safeguardFamily.diabezone.apiService

import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.AppointmentResponse
import com.safeguardFamily.diabezone.model.response.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("providers")
    fun getAppointmentDate(@Body body: IdRequest): Call<BaseResponse<AppointmentResponse>>

}