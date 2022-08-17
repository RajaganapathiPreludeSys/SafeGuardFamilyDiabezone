package com.safeguardFamily.diabezone

import android.app.Application
import com.safeguardFamily.diabezone.common.SharedPref

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPref.init(applicationContext)
    }

}