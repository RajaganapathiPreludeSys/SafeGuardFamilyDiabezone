package com.safeguardFamily.diabezone.ui.fragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
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
            navigateTo(BookingDetailsActivity::class.java)
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

        val callHealthCoach = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted)
                startActivity(
                    Intent(
                        Intent.ACTION_CALL,
                        Uri.parse("tel:+91" + viewModel.userResponse.value!!.health_coach!!.mobile)
                    )
                )
            else showToast("Permission Denied by user for making calls")
        }

        val callSupport = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted)
                startActivity(
                    Intent(
                        Intent.ACTION_CALL,
                        Uri.parse("tel:+91" + viewModel.userResponse.value!!.contactInfo!!.mobile)
                    )
                )
            else showToast("Permission Denied by user for making calls")
        }

        mBinding.clContact.setOnClickListener {
            if (SharedPref.isMember()) {
                callHealthCoach.launch(Manifest.permission.CALL_PHONE)
            } else {
                showToast("Only members can contact their Health Coach")
                navigateTo(SubscriptionActivity::class.java)
            }
        }

        mBinding.clPastConsult.setOnClickListener { logout() }

        mBinding.clLogout.setOnClickListener { logout() }

        mBinding.ivCall.setOnClickListener {
            callSupport.launch(Manifest.permission.CALL_PHONE)
        }

        mBinding.ivWhatsApp.setOnClickListener {
            openWhatsApp(viewModel.userResponse.value!!.contactInfo!!.whatsappNo!!)
        }

        mBinding.ivEmail.setOnClickListener {
            openMail(
                viewModel.userResponse.value!!.contactInfo!!.email!!,
                "Request for support",
                ""
            )
        }

        Glide.with(this)
            .load(viewModel.userResponse.value!!.contactInfo!!.pic!!)
            .placeholder(R.drawable.ic_profile_thumb)
            .into(mBinding.ivContactImage)

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