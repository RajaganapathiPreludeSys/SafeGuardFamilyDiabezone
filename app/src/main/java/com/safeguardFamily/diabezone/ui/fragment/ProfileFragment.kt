package com.safeguardFamily.diabezone.ui.fragment

import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.URL_ABOUT
import com.safeguardFamily.diabezone.common.Bundle.URL_TERMS
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.FragmentProfileBinding
import com.safeguardFamily.diabezone.ui.activity.*
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
            mBundle.putString(Bundle.KEY_WEB_KEY, "Terms and Service")
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

            if (SharedPref.isMember()) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data =
                    Uri.parse("tel:" + viewModel.userResponse.value!!.health_coach!!.mobile)
                startActivity(dialIntent)
            } else {
                showToast("Only members can contact their Health Coach")
                navigateTo(SubscriptionActivity::class.java)
            }
        }

        mBinding.clLogout.setOnClickListener { logout() }

        mBinding.rlDiabetes.setOnClickListener {
            val mBundle = android.os.Bundle()
            mBundle.putBoolean(Bundle.KEY_EDIT_PROFILE, true)
            navigateTo(RegisterActivity::class.java, mBundle)
        }

    }

    override fun onResume() {
        super.onResume()
        mBinding.profile = SharedPref.getUser()
    }

}