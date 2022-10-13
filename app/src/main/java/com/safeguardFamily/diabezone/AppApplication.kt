package com.safeguardFamily.diabezone

import android.app.AlertDialog
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.safeguardFamily.diabezone.apiService.RetrofitClient
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.model.request.InitRequest
import com.safeguardFamily.diabezone.model.response.BaseResponse
import com.safeguardFamily.diabezone.model.response.InitResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: AppApplication? = null
        fun applicationContext(): Context = instance!!.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        SharedPref.init(applicationContext)
        applicationContext()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                android.util.Log.d("Token", "Failed:")
                return@OnCompleteListener
            }

            val token = task.result
            android.util.Log.d("Token", "AAA : $token")
            initAppAPI(token)
        })

    }

    private fun initAppAPI(token: String) {
        RetrofitClient.apiInterface.postInit(
            InitRequest(
                uid = SharedPref.getUserId()!!,
                platform = "android",
                version = BuildConfig.VERSION_NAME,
                token = token
            )
        )
            .enqueue(object : Callback<BaseResponse<InitResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<InitResponse>>,
                    response: Response<BaseResponse<InitResponse>>
                ) {
                    if (response.isSuccessful)
                        if (response.body()?.success!! && response.body()!!.data.forceUpdate == true) {
                            updateApp()
                        }
                }

                override fun onFailure(
                    call: Call<BaseResponse<InitResponse>>,
                    t: Throwable
                ) {

                }
            })
    }

    fun updateApp() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder
                    .setTitle("Force Update")
                    .setMessage("Few features are updated in our application. To avail them please update our application")
                    .setCancelable(false)
                    .setPositiveButton("Update") { dialog, id ->
                        try {
                            startActivity(
                                Intent(
                                    "android.intent.action.VIEW",
                                    Uri.parse("market://details?id=$packageName")
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    "android.intent.action.VIEW",
                                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                )
                            )
                        }
                    }
                val alert = dialogBuilder.create()
                alert.setTitle("Payment for Appointment")
                alert.show()
            }
        }
    }

}