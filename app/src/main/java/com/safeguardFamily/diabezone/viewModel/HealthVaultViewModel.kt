package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.request.IdRequest
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.HealthVaultResponse
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.model.response.Vault
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthVaultViewModel : BaseViewModel() {
    var healthVault = MutableLiveData<Vault?>()
    var healthCoach = MutableLiveData<Provider>()
    var isSample = MutableLiveData<Boolean?>()
    var pdfURL = ""

    init {
        getHealthVault()
    }

    private fun getHealthVault() {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.getHealthVault(IdRequest(uid = SharedPref.getUserId()!!))
            .enqueue(object : Callback<BaseResponse<HealthVaultResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<HealthVaultResponse>>,
                    response: Response<BaseResponse<HealthVaultResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            healthVault.postValue(response.body()!!.data.vault)
                            healthCoach.postValue(response.body()!!.data.healthCoach!!)
                            isSample.postValue(response.body()!!.data.isSample)
                            pdfURL = response.body()!!.data.pdfUrl!!
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<HealthVaultResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }
}