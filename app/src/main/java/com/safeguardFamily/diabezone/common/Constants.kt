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
    const val KEY_EDIT_PROFILE = "KEY_EDIT_PROFILE"
    const val KEY_WEB_KEY = "KEY_WEB_KEY"
    const val KEY_WEB_URL = "KEY_WEB_URL"
    const val KEY_REGISTER_PHONE = "KEY_REGISTER_PHONE"
    const val KEY_OTPs = "KEY_OTPs"
    const val KEY_DOCTOR = "KEY_DOCTOR"
    const val KEY_TITLE = "KEY_TITLE"
    const val KEY_APPOINTMENT = "KEY_APPOINTMENT"
    const val KEY_BOOKING_DETAILS = "KEY_BOOKING_DETAILS"

    const val DATE_FORMAT = "LLL dd, yyyy"
    const val DATE_TIME_FORMAT = "LLL dd, yyyy hh:mm a"
    const val API_DATE_FORMAT = "yyyy-MM-dd"
    const val FROM_API_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val date12Format = "hh:mm a"
    const val date24Format = "HH:mm"

    const val URL_TERMS = "https://diabezone.com/terms-conditions"
    const val URL_ABOUT = "https://diabezone.com/about-us"
    const val URL_PRIVACY = "https://diabezone.com/privacy-policy"

}
