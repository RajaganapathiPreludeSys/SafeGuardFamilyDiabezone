package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.request.PaymentFailRequest
import com.safeguardFamily.diabezone.model.response.AppointmentResponse
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.SubscribeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentPaymentViewModel : BaseViewModel() {

    val appointment = MutableLiveData<AppointmentResponse>()
    val isBookingCompleted = MutableLiveData<Boolean>()

    fun createAppointment(
        request: CreateAppointmentRequest,
        onSuccess: (() -> Unit)
    ) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.CONTENT, "Create Appointment")
        }
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.createAppointment(request)
            .enqueue(object : Callback<BaseResponse<AppointmentResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    response: Response<BaseResponse<AppointmentResponse>>
                ) {
                    isBookingCompleted.postValue(false)
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            appointment.postValue(response.body()!!.data)
                            if (response.body()!!.data!!.appointment.booking_status == 4)
                                onSuccess()
                            else if (response.body()!!.data!!.appointment.booking_status == 1) {
//                                successToast.postValue("Appointment Created Successfully")
                                isBookingCompleted.postValue(true)
                            }
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                    isBookingCompleted.postValue(false)
                }

            })
    }

    fun reScheduleAppointment(
        request: CreateAppointmentRequest,
    ) {

        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.CONTENT, "Reschedule Appointment")
        }
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.reScheduleAppointment(request)
            .enqueue(object : Callback<BaseResponse<AppointmentResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    response: Response<BaseResponse<AppointmentResponse>>
                ) {
                    isBookingCompleted.postValue(false)
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            appointment.postValue(response.body()!!.data)
                            isBookingCompleted.postValue(true)
                            successToast.postValue("Appointment Rescheduled Successfully")
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                    isBookingCompleted.postValue(false)
                }

            })
    }

    fun confirmAppointment(
        request: CreateAppointmentRequest,
    ) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.CONTENT, "Create Appointment")
        }
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.confirmAppointment(request)
            .enqueue(object : Callback<BaseResponse<AppointmentResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    response: Response<BaseResponse<AppointmentResponse>>
                ) {
                    isBookingCompleted.postValue(false)
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            appointment.postValue(response.body()!!.data)
                            isBookingCompleted.postValue(true)
//                            successToast.postValue("Appointment Created Successfully")
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                    isBookingCompleted.postValue(false)
                }

            })
    }

    fun payFailed(request: PaymentFailRequest) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.CONTENT, "Payment Failed")
        }
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.payFailed(request)
            .enqueue(object : Callback<BaseResponse<SubscribeResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SubscribeResponse>>,
                    response: retrofit2.Response<BaseResponse<SubscribeResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {

                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<SubscribeResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }
}