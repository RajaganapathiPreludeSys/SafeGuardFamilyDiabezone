package com.safeguardFamily.diabezone.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
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
import org.json.JSONException

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(
    R.layout.fragment_profile,
    ProfileViewModel::class.java
) {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        viewModel = (activity as DashboardActivity).mViewModel

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == 123 && result.data != null) {
                    (activity as DashboardActivity?)!!.setCurrentFragment(
                        (activity as DashboardActivity?)!!.appointment
                    )
                }
            }

        mBinding.rlBookingContainer.setOnClickListener {
            activityResultLauncher.launch(
                Intent(
                    requireContext(),
                    MemberDetailsActivity::class.java
                )
            )
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Member details from profile")
            }
        }

        mBinding.clTermsService.setOnClickListener {
            val mBundle = android.os.Bundle()
            mBundle.putString(Bundle.KEY_WEB_KEY, "Terms and Service")
            mBundle.putString(Bundle.KEY_WEB_URL, URL_TERMS)
            navigateTo(WebViewActivity::class.java, mBundle)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Terms and services in webview")
            }
        }

        mBinding.clAbout.setOnClickListener {
            val mBundle = android.os.Bundle()
            mBundle.putString(Bundle.KEY_WEB_KEY, "About Diabezone")
            mBundle.putString(Bundle.KEY_WEB_URL, URL_ABOUT)
            navigateTo(WebViewActivity::class.java, mBundle)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "About in webview")
            }
        }

        mBinding.ivProgram.setOnClickListener {
            navigateTo(SubscriptionActivity::class.java)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Subscription Page")
            }
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
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Contact screen from profile")
            }
        }

        mBinding.clLogout.setOnClickListener {
            logout()
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Logout")
            }
        }

        mBinding.ivCall.setOnClickListener {
            callSupport.launch(Manifest.permission.CALL_PHONE)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Calling customer support")
            }
        }

        mBinding.ivWhatsApp.setOnClickListener {
            openWhatsApp(viewModel.userResponse.value!!.contactInfo!!.whatsappNo!!)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Contacting customer support in whatsapp")
            }
        }

        mBinding.ivEmail.setOnClickListener {
            openMail(
                viewModel.userResponse.value!!.contactInfo!!.email!!,
                "Request for support",
                ""
            )
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Contacting customer support via mail")
            }
        }

        try {
            Glide.with(this)
                .load(viewModel.userResponse.value!!.contactInfo!!.pic!!)
                .placeholder(R.drawable.ic_profile_thumb)
                .into(mBinding.ivContactImage)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mBinding.rlDiabetes.setOnClickListener {
            val mBundle = android.os.Bundle()
            mBundle.putBoolean(Bundle.KEY_EDIT_PROFILE, true)
            navigateTo(RegisterActivity::class.java, mBundle)

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Edit profile")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.profile = SharedPref.getUser()

        val spanString = SpannableString("UHID: " + SharedPref.getUser().uname)
        spanString.setSpan(StyleSpan(Typeface.BOLD), 0, 5, 0)

        mBinding.tvUhid.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvUhid.setText(spanString, TextView.BufferType.SPANNABLE)
    }
}