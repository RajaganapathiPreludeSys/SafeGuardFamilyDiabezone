package com.safeguardFamily.diabezone.apiService

import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.request.GetSlotsRequest
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("providers")
    fun getAppointmentDate(@Body body: IdRequest): Call<BaseResponse<ProvidersResponse>>

    @POST("create-appointment")
    fun createAppointment(@Body body: CreateAppointmentRequest): Call<BaseResponse<AppointmentResponse>>

    @POST("reshedule-appointment")
    fun reScheduleAppointment(@Body body: CreateAppointmentRequest): Call<BaseResponse<AppointmentResponse>>

    @POST("provider-slots")
    fun getSlots(@Body body: GetSlotsRequest): Call<BaseResponse<SlotsResponse>>

}