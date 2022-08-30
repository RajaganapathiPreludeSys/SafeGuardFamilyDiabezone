package com.safeguardFamily.diabezone.apiService

import com.safeguardFamily.diabezone.model.request.*
import com.safeguardFamily.diabezone.model.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    //    https://safeguardfamily.com/apis/send-otp
    @POST("send-otp")
    fun postOtp(@Body body: MobileNumberRequest): Call<BaseResponse<OtpResponse>>

    //    https://safeguardfamily.com/apis/verify-otp
    @POST("verify-otp")
    fun postVerifyOtp(@Body body: MobileNumberRequest): Call<BaseResponse<UserResponse>>

    //    https://safeguardfamily.com/apis/main
    @POST("main")
    fun getHome(@Body body: IdRequest): Call<BaseResponse<HomeResponse>>

    //    https://safeguardfamily.com/apis/update-user
    @POST("update-user")
    fun postUpdateUser(@Body body: User): Call<BaseResponse<UserResponse>>

    @Multipart
    @POST("update-user-details")
    fun multipartUpdateUser(
        @Part data: MultipartBody.Part,
        @Part pic: MultipartBody.Part?
    ): Call<BaseResponse<UserResponse>>

    //  https://safeguardfamily.com/apis/add-diabetes-log
    @POST("add-diabetes-log")
    fun addEditDiabetesLog(@Body body: DiabetesLogRequest): Call<BaseResponse<DiabetesLogResponse>>

    //  https://safeguardfamily.com/apis/diabetes-logs
    @POST("diabetes-logs")
    fun getLogs(@Body body: IdRequest): Call<BaseResponse<DiabetesResponse>>

    @POST("providers")
    fun getAppointmentDate(@Body body: IdRequest): Call<BaseResponse<ProvidersResponse>>

    @POST("create-appointment")
    fun createAppointment(@Body body: CreateAppointmentRequest): Call<BaseResponse<AppointmentResponse>>

    @POST("reshedule-appointment")
    fun reScheduleAppointment(@Body body: CreateAppointmentRequest): Call<BaseResponse<AppointmentResponse>>

    @POST("provider-slots")
    fun getSlots(@Body body: GetSlotsRequest): Call<BaseResponse<SlotsResponse>>

    @POST("profile")
    fun getProfile(@Body body: IdRequest): Call<BaseResponse<ProfileResponse>>

}