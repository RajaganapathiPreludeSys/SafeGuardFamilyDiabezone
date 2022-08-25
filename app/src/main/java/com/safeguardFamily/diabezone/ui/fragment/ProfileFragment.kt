package com.safeguardFamily.diabezone.ui.fragment

import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.URL_ABOUT
import com.safeguardFamily.diabezone.common.Bundle.URL_TERMS
import com.safeguardFamily.diabezone.databinding.FragmentProfileBinding
import com.safeguardFamily.diabezone.ui.activity.BookingDetailsActivity
import com.safeguardFamily.diabezone.ui.activity.DashboardActivity
import com.safeguardFamily.diabezone.ui.activity.RegisterActivity
import com.safeguardFamily.diabezone.ui.activity.WebViewActivity
import com.safeguardFamily.diabezone.viewModel.DashboardViewModel
import com.safeguardFamily.diabezone.viewModel.ProfileViewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(
    R.layout.fragment_profile,
    ProfileViewModel::class.java
) {
    private lateinit var viewModel: DashboardViewModel

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        viewModel = (activity as DashboardActivity).mViewModel
        mBinding.profile = viewModel.userResponse.value

        mBinding.rlBookingContainer.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(
                Bundle.KEY_BOOKING_DETAILS,
                Gson().toJson(viewModel.userResponse.value)
            )
            navigateTo(BookingDetailsActivity::class.java, bundle)
        }

        mBinding.clTermsService.setOnClickListener {
            val mBundle = android.os.Bundle()
            mBundle.putString(Bundle.KEY_WEB_KEY, "Teams and Service")
            mBundle.putString(Bundle.KEY_WEB_URL, URL_TERMS)
            navigateTo(WebViewActivity::class.java, mBundle)
        }

        mBinding.clAbout.setOnClickListener {
            val mBundle = android.os.Bundle()
            mBundle.putString(Bundle.KEY_WEB_KEY, "About Diabezone")
            mBundle.putString(Bundle.KEY_WEB_URL, URL_ABOUT)
            navigateTo(WebViewActivity::class.java, mBundle)
        }

        mBinding.clContact.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data =
                Uri.parse("tel:" + viewModel.userResponse.value!!.health_coach!!.mobile)
            startActivity(dialIntent)
        }

        mBinding.clLogout.setOnClickListener { logout() }

        mBinding.ivEdit.setOnClickListener {
            val mBundle = android.os.Bundle()
            mBundle.putBoolean(Bundle.KEY_EDIT_PROFILE, true)
            navigateTo(RegisterActivity::class.java, mBundle)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getProfile()
    }

}