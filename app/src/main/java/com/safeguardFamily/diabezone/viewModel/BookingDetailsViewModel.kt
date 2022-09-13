package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingDetailsViewModel : BaseViewModel() {
    var userResponse = MutableLiveData<ProfileResponse>()

    init {
        getProfile()
    }

    private fun getProfile() {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.getProfile(IdRequest(uid = SharedPref.getUserId()!!))
            .enqueue(object : Callback<BaseResponse<ProfileResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<ProfileResponse>>,
                    response: Response<BaseResponse<ProfileResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            userResponse.postValue(response.body()?.data!!)
                            SharedPref.putProfileDetails(response.body()?.data!!)
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<ProfileResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }
}