package com.safeguardFamily.diabezone.viewModel

import com.safeguardFamily.diabezone.apiService.NoConnectivityException
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.model.request.MobileNumberRequest
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.OtpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileViewModel : BaseViewModel() {

    fun getOtp(mobile: String, onSuccess: ((otp: List<String>) -> Unit)) {
        apiLoader.postValue(true)
        val request = MobileNumberRequest()
        request.mobile = mobile
        RetrofitClient.apiInterface.postOtp(request)
            .enqueue(object : Callback<BaseResponse<OtpResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<OtpResponse>>,
                    response: Response<BaseResponse<OtpResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            onSuccess(response.body()?.data!!.otps!!)
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<OtpResponse>>,
                    t: Throwable
                ) {
                    if (t is NoConnectivityException){

                    }
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }

}