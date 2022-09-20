package com.safeguardFamily.diabezone

import android.app.Application
import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.safeguardFamily.diabezone.common.GlideImageLoader
import com.safeguardFamily.diabezone.common.SharedPref
import lv.chi.photopicker.ChiliPhotoPicker

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
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.safeguardFamily.diabezone"
        )
        applicationContext()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                android.util.Log.d("Token", "Failed:")
                return@OnCompleteListener
            }

            val token = task.result
            android.util.Log.d("Token", "AAA : $token")
        })

    }

}