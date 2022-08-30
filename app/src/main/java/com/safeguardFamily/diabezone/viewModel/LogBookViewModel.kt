package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.DiabetesLogResponse
import com.safeguardFamily.diabezone.model.response.DiabetesResponse
import com.safeguardFamily.diabezone.model.response.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogBookViewModel : BaseViewModel() {

    var logs = MutableLiveData<List<Log>>()

    init {
        getLogBook()
    }

    private fun getLogBook() {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.getLogs((IdRequest(uid = SharedPref.getUserId()!!)))
//        RetrofitClient.apiInterface.getLogs(IdRequest(uid = "166080591250800878"))
            .enqueue(object : Callback<BaseResponse<DiabetesResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<DiabetesResponse>>,
                    response: Response<BaseResponse<DiabetesResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()!!.success!!) logs.postValue(response.body()!!.data!!.logs!!)
                        else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<DiabetesResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }

    fun editDiabetesLog(request: DiabetesLogRequest) {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.addEditDiabetesLog(request)
            .enqueue(object : Callback<BaseResponse<DiabetesLogResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<DiabetesLogResponse>>,
                    response: Response<BaseResponse<DiabetesLogResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()!!.success!!) getLogBook()
                        else apiError.postValue(response.body()!!.error)
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