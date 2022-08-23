package com.safeguardFamily.diabezone.ui.activity

import android.util.Log
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.databinding.ActivityBookingDetailsBinding
import com.safeguardFamily.diabezone.model.response.ProfileResponse
import com.safeguardFamily.diabezone.viewModel.BookingDetailsViewModel

class BookingDetailsActivity : BaseActivity<ActivityBookingDetailsBinding, BookingDetailsViewModel>(
    R.layout.activity_booking_details,
    BookingDetailsViewModel::class.java
) {

    lateinit var userResponse: ProfileResponse

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }

        if (intent.extras?.containsKey(Bundle.KEY_BOOKING_DETAILS) == true) {
            userResponse = Gson().fromJson(
                intent.extras?.getString(Bundle.KEY_BOOKING_DETAILS),
                ProfileResponse::class.java
            )
            mBinding.profile = userResponse
        }

        mBinding.rlDoctorContainer.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(userResponse.health_coach))
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

    }
}