package com.safeguardFamily.diabezone.viewModel

import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.request.PaymentFailRequest
import com.safeguardFamily.diabezone.model.request.SubscriptionRequest
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.ProgramsResponse
import com.safeguardFamily.diabezone.model.response.SubscribeResponse
import retrofit2.Call
import retrofit2.Callback

class SubscriptionViewModel : BaseViewModel() {

    fun getPrograms(onSuccess: ((response: ProgramsResponse) -> Unit)) {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.getPrograms(IdRequest(uid = SharedPref.getUserId()!!))
            .enqueue(object : Callback<BaseResponse<ProgramsResponse>> {

                override fun onFailure(
                    call: Call<BaseResponse<ProgramsResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }

                override fun onResponse(
                    call: Call<BaseResponse<ProgramsResponse>>,
                    response: retrofit2.Response<BaseResponse<ProgramsResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            onSuccess(response.body()!!.data!!)
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

            })
    }

    fun subscribe(
        request: SubscriptionRequest,
        onSuccess: ((response: SubscribeResponse) -> Unit)
    ) {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.subscribe(request)
            .enqueue(object : Callback<BaseResponse<SubscribeResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SubscribeResponse>>,
                    response: retrofit2.Response<BaseResponse<SubscribeResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            onSuccess(response.body()!!.data!!)
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