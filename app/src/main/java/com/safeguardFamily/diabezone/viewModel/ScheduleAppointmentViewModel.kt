package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.response.AppointmentResponse
import com.safeguardFamily.diabezone.model.response.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleAppointmentViewModel : BaseViewModel() {

    val appointment = MutableLiveData<AppointmentResponse>()
    val isBookingCompleted = MutableLiveData<Boolean>()

    fun createAppointment(
        request: CreateAppointmentRequest,
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

}