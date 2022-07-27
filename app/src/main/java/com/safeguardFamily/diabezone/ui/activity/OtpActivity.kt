package com.safeguardFamily.diabezone.ui.activity

import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.viewModel.OtpViewModel
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.databinding.ActivityOtpBinding

class OtpActivity : BaseActivity<ActivityOtpBinding, OtpViewModel>(
    R.layout.activity_otp,
    OtpViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
    }
}