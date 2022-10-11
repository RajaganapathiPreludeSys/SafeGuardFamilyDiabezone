package com.safeguardFamily.diabezone.ui.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.safeguardFamily.diabezone.BuildConfig
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle.KEY_OTPs
import com.safeguardFamily.diabezone.common.Bundle.KEY_REGISTER_PHONE
import com.safeguardFamily.diabezone.databinding.ActivityMobileBinding
import com.safeguardFamily.diabezone.viewModel.MobileViewModel

class MobileActivity : BaseActivity<ActivityMobileBinding, MobileViewModel>(
    R.layout.activity_mobile,
    MobileViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.tvTitle.text = "Login with Mobile"
        mBinding.icHeader.ivBack.visibility = View.INVISIBLE

        if (BuildConfig.BUILD_TYPE == "debug") mBinding.tiePhone.setText("9988776650")

        mBinding.btSendCode.setOnClickListener {
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
                param(FirebaseAnalytics.Param.CONTENT, "send code")
            }
            if (mBinding.tiePhone.text.toString().matches(Regex("[0-9]{10}"))) {
                mViewModel.getOtp(mBinding.tiePhone.text.toString()) {
                    val mBundle = android.os.Bundle()
                    mBundle.putString(KEY_REGISTER_PHONE, mBinding.tiePhone.text!!.toString())
                    mBundle.putString(KEY_OTPs, Gson().toJson(it))

                    navigateTo(OtpActivity::class.java, mBundle)
                }

            } else mBinding.textInputLayout.error = getString(R.string.valid_phone)
        }

        mBinding.tiePhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().matches(Regex("[0-9]{10}"))) {
                    mBinding.textInputLayout.error = null
                    mBinding.btSendCode.isEnabled = true
                } else {
                    mBinding.textInputLayout.error = getString(R.string.valid_phone)
                    mBinding.btSendCode.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}