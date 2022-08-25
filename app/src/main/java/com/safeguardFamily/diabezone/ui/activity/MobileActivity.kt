package com.safeguardFamily.diabezone.ui.activity

import android.text.Editable
import android.text.TextWatcher
import com.google.gson.Gson
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

        mBinding.btSendCode.setOnClickListener {
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
                mBinding.textInputLayout.error =
                    if (p0.toString().matches(Regex("[0-9]{10}"))) null
                    else getString(R.string.valid_phone)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}