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
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.viewModel.OtpViewModel
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.KEY_REGISTER_PHONE
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.databinding.ActivityOtpBinding
import com.safeguardFamily.diabezone.ui.pin.OtpPinView

class OtpActivity : BaseActivity<ActivityOtpBinding, OtpViewModel>(
    R.layout.activity_otp,
    OtpViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
        val extras = intent.extras
        mBinding.tvWelcomeDesc.text =
            getString(R.string.otp_code_desc, extras?.getString(KEY_REGISTER_PHONE))

        mBinding.ivBack.setOnClickListener { finish() }
        val spanString = SpannableString(getString(R.string.accept_terms_and_privacy))

        val termsAndCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val mBundle = Bundle()
                mBundle.putString(KEY_WEB_KEY, "Teams and Conditions")
                navigateTo(WebViewActivity::class.java, mBundle)
            }
        }

        val privacy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val mBundle = Bundle()
                mBundle.putString(KEY_WEB_KEY, "Privacy Policy")
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

        mBinding.tvResendCode.setOnClickListener { showToast("New OTP sent to your number") }

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
            if (mBinding.pvOtp.value == "1234") {
                navigateTo(RegisterActivity::class.java)
            } else showToast("Invalid OTP")
        }
    }

}