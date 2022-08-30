package com.safeguardFamily.diabezone.viewModel

import com.google.gson.Gson
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.User
import com.safeguardFamily.diabezone.model.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RegisterViewModel : BaseViewModel() {

    fun updateUser(user: User, onSuccess: ((response: UserResponse) -> Unit)) {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.postUpdateUser(user)
            .enqueue(object : Callback<BaseResponse<UserResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<UserResponse>>,
                    response: Response<BaseResponse<UserResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!!) {
                            SharedPref.putUser(response.body()!!.data!!.user)
                            onSuccess(response.body()?.data!!)
                        } else apiError.postValue(response.body()!!.error)
                    else apiError.postValue(response.message())
                    apiLoader.postValue(false)
                }

                override fun onFailure(
                    call: Call<BaseResponse<UserResponse>>,
                    t: Throwable
                ) {
                    apiError.postValue(t.message)
                    apiLoader.postValue(false)
                }
            })
    }

    fun multiPartUser(user: User, imageUri: File, onSuccess: ((response: UserResponse) -> Unit)) {
        apiLoader.postValue(true)
        RetrofitClient.apiInterface.multipartUpdateUser(
            MultipartBody.Part
                .createFormData(
                    "data",
                    Gson().toJson(user)
                ),
            MultipartBody.Part
                .createFormData(
                    name = "pic",
                    filename = imageUri.name,
                    body = imageUri.asRequestBody()
                )
        ).enqueue(object : Callback<BaseResponse<UserResponse>> {
            override fun onResponse(
                call: Call<BaseResponse<UserResponse>>,
                response: Response<BaseResponse<UserResponse>>
            ) {
                if (response.isSuccessful)
                    if (response.body()?.success!!) {
                        SharedPref.putUser(response.body()!!.data!!.user)
                        onSuccess(response.body()?.data!!)
                    } else apiError.postValue(response.body()!!.error)
                else apiError.postValue(response.message())
                apiLoader.postValue(false)
            }

            override fun onFailure(
                call: Call<BaseResponse<UserResponse>>,
                t: Throwable
            ) {
                t.printStackTrace()
                apiError.postValue(t.message)
                apiLoader.postValue(false)
            }
        })
    }
}