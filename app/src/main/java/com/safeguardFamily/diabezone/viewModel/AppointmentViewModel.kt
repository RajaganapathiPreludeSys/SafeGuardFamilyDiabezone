package com.safeguardFamily.diabezone.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.AppointmentResponse
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.Provider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentViewModel : BaseViewModel() {

    val providers = MutableLiveData<List<Provider>>()

    fun getAppointmentData() {
        val request = IdRequest()
        RetrofitClient.apiInterface.getAppointmentDate(request)
            .enqueue(object : Callback<BaseResponse<AppointmentResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    response: Response<BaseResponse<AppointmentResponse>>
                ) {
                    Log.d("RRR", "onResponse() called with: call = $call, response = $response")
                    Log.d(
                        "RRR", "onResponse() called with: call = $call, " +
                                "response = ${Gson().toJson(response.body())}"
                    )
                    if (response.body()?.success!!)
                        providers.postValue(response.body()!!.data!!.providers)
                    else apiError.postValue(response.body()!!.error)
                }

                override fun onFailure(
                    call: Call<BaseResponse<AppointmentResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                }

            })
    }
}
