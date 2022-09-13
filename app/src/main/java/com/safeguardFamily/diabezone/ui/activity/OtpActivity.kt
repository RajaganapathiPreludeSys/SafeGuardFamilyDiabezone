package com.safeguardFamily.diabezone.ui.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.safeguardFamily.diabezone.BuildConfig
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle.KEY_OTPs
import com.safeguardFamily.diabezone.common.Bundle.KEY_REGISTER_PHONE
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_URL
import com.safeguardFamily.diabezone.common.Bundle.URL_PRIVACY
import com.safeguardFamily.diabezone.common.Bundle.URL_TERMS
import com.safeguardFamily.diabezone.databinding.ActivityOtpBinding
import com.safeguardFamily.diabezone.ui.pin.OtpPinView
import com.safeguardFamily.diabezone.viewModel.OtpViewModel

class OtpActivity : BaseActivity<ActivityOtpBinding, OtpViewModel>(
    R.layout.activity_otp,
    OtpViewModel::class.java
) {
    private lateinit var otp: List<String>
    private var mobileNumber: String? = ""

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
        val extras = intent.extras
        mBinding.icHeader.tvTitle.text = "Verify Account!"

        var mText = ""
        if (extras?.containsKey(KEY_REGISTER_PHONE) == true) {
            mobileNumber = extras.getString(KEY_REGISTER_PHONE)
            mText = getString(R.string.otp_code_desc, mobileNumber)
        }

        val mSS = SpannableString(mText)

        val goBack: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                finish()
            }
        }
        mSS.setSpan(goBack, 52, 59, 0)
        mSS.setSpan(ForegroundColorSpan(getColor(R.color.blue)), 52, 59, 0)
        mSS.setSpan(StyleSpan(Typeface.BOLD), 52, 59, 0)
        mBinding.tvWelcomeDesc.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvWelcomeDesc.text = mSS
        mBinding.tvWelcomeDesc.isSelected = true

        if (extras?.containsKey(KEY_OTPs) == true)
            otp = Gson().fromJson(extras.getString(KEY_OTPs), Array<String>::class.java).toList()

        mBinding.icHeader.ivBack.setOnClickListener { finish() }
        val spanString = SpannableString(getString(R.string.accept_terms_and_privacy))

        val termsAndCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val mBundle = Bundle()
                mBundle.putString(KEY_WEB_KEY, "Terms and Service")
                mBundle.putString(KEY_WEB_URL, URL_TERMS)
                navigateTo(WebViewActivity::class.java, mBundle)
            }
        }

        val privacy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val mBundle = Bundle()
                mBundle.putString(KEY_WEB_KEY, "Privacy Policy")
                mBundle.putString(KEY_WEB_URL, URL_PRIVACY)
                navigateTo(WebViewActivity::class.java, mBundle)
            }
        }

        spanString.setSpan(termsAndCondition, 57, 77, 0)
        spanString.setSpan(privacy, 38, 52, 0)
        spanString.setSpan(ForegroundColorSpan(Color.BLACK), 57, 77, 0)
        spanString.setSpan(ForegroundColorSpan(Color.BLACK), 38, 52, 0)
        spanString.setSpan(UnderlineSpan(), 57, 77, 0)
        spanString.setSpan(UnderlineSpan(), 38, 52, 0)
        spanString.setSpan(StyleSpan(Typeface.BOLD), 57, 77, 0)
        spanString.setSpan(StyleSpan(Typeface.BOLD), 38, 52, 0)

        mBinding.tvTermsPrivacy.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvTermsPrivacy.setText(spanString, TextView.BufferType.SPANNABLE)
        mBinding.tvTermsPrivacy.isSelected = true

        mBinding.tvResendCode.setOnClickListener {
            mViewModel.getOtp(mobileNumber!!) { otp = it }
        }

        if (BuildConfig.BUILD_TYPE == "debug") {
            mBinding.pvOtp.value = otp[0]
            mBinding.btProceed.isEnabled = true
        }

        mBinding.pvOtp.setPinViewEventListener(object : OtpPinView.PinViewEventListener {
            override fun onDataEntered(otpPinView: OtpPinView?, fromUser: Boolean) {
                mBinding.btProceed.isEnabled = true
                Log.d(
                    "TAG",
                    "onDataEntered() called with: pinview = ${otpPinView!!.value}, fromUser = $fromUser"
                )
            }
        })

        mBinding.btProceed.setOnClickListener {
            if (otp.contains(mBinding.pvOtp.value)) {
                mViewModel.verifyOtp(mobileNumber!!, mBinding.pvOtp.value) {
                    navigateTo(if (it) RegisterActivity::class.java else DashboardActivity::class.java)
                    finishAffinity()
                }
            } else showToast("Invalid OTP")
        }

        mBinding.pvOtp.requestPinEntryFocus()
    }

}