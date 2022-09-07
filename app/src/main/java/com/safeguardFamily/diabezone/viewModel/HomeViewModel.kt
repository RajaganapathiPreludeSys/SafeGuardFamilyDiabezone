package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : BaseViewModel() {

    val notifications = MutableLiveData<List<Notification>>()

    fun getHome(onSuccess: ((response: Graph) -> Unit)) {
        RetrofitClient.apiInterface.getHome(IdRequest(uid = SharedPref.getUserId()!!))
            .enqueue(object : Callback<BaseResponse<HomeResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<HomeResponse>>,
                    response: Response<BaseResponse<HomeResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()!!.success!!) {
                            notifications.postValue(response.body()!!.data!!.notifications)
                            onSuccess(response.body()!!.data!!.graph!!)
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                }

                override fun onFailure(
                    call: Call<BaseResponse<HomeResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                }
            })
    }

    fun addDiabetesLog(request: DiabetesLogRequest, onSuccess: (() -> Unit)) {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.addEditDiabetesLog(request)
            .enqueue(object : Callback<BaseResponse<DiabetesLogResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<DiabetesLogResponse>>,
                    response: Response<BaseResponse<DiabetesLogResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()!!.success!!) {
                            onSuccess()
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<DiabetesLogResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }
}