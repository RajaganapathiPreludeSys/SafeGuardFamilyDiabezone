package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.Appointment
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.model.response.ProvidersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentViewModel : BaseViewModel() {

    val providers = MutableLiveData<List<Provider>>()
    val upcomingAppointment = MutableLiveData<List<Appointment>>()

    fun getAppointmentData() {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.getAppointmentDate(IdRequest(uid = SharedPref.getUserId()!!))
            .enqueue(object : Callback<BaseResponse<ProvidersResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<ProvidersResponse>>,
                    response: Response<BaseResponse<ProvidersResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            providers.postValue(response.body()!!.data.providers)
                            upcomingAppointment.postValue(response.body()!!.data.appointments)
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<ProvidersResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }
}
