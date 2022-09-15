package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.request.GetSlotsRequest
import com.safeguardFamily.diabezone.model.request.PaymentFailRequest
import com.safeguardFamily.diabezone.model.response.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleAppointmentViewModel : BaseViewModel() {

    val appointment = MutableLiveData<AppointmentResponse>()
    val isBookingCompleted = MutableLiveData<Boolean>()

    fun getSlots(request: GetSlotsRequest, onSuccess: ((slots: List<AvailableSlot>?) -> Unit)) {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.getSlots(request)
            .enqueue(object : Callback<BaseResponse<SlotsResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SlotsResponse>>,
                    response: Response<BaseResponse<SlotsResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            onSuccess(response.body()?.data?.slots)
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<SlotsResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }

    fun createAppointment(
        request: CreateAppointmentRequest,
        onSuccess: (() -> Unit)
    ) {
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
                            isBookingCompleted.postValue(true)
                            if (response.body()!!.data!!.appointment.booking_status == 4)
                                onSuccess()
                            else if (response.body()!!.data!!.appointment.booking_status == 1)
                                successToast.postValue("Appointment Created Successfully")
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
                            successToast.postValue("Appointment Created Successfully")
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