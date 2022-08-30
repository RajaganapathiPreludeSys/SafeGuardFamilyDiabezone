package com.safeguardFamily.diabezone

import android.app.Application
import com.safeguardFamily.diabezone.common.GlideImageLoader
import com.safeguardFamily.diabezone.common.SharedPref
import lv.chi.photopicker.ChiliPhotoPicker

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPref.init(applicationContext)
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.safeguardFamily.diabezone"
        )
    }

}