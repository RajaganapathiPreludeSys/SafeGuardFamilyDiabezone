package com.safeguardFamily.diabezone.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.model.request.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    fun getServicesApiCall() {
        RetrofitClient.apiInterface.getUsers().enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                Log.v("DEBUG : ", response.body().toString())
            }
        })
    }
}