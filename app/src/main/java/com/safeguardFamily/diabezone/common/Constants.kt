package com.safeguardFamily.diabezone.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class Constants {
    companion object {
        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(view: ImageView, image: String?) {
            Glide.with(view.context)
                .load(image)
                .into(view)
        }
    }
}

object Bundle {
    const val TAG = "RRR :: "
    const val KEY_WEB_KEY = "KEY_WEB_KEY"
    const val KEY_WEB_URL = "KEY_WEB_URL"
    const val KEY_REGISTER_NAME = "KEY_REGISTER_NAME"
    const val KEY_REGISTER_PHONE = "KEY_REGISTER_PHONE"
    const val KEY_DOCTOR = "KEY_DOCTOR"

    const val DATE_FORMAT = "LLL dd, yyyy"
    const val API_DATE_FORMAT = "yyyy-MM-dd"
    const val date12Format = "hh:mm a"
    const val date24Format = "HH:mm"
}
