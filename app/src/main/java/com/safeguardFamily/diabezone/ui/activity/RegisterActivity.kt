package com.safeguardFamily.diabezone.ui.activity

import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.viewModel.RegisterViewModel
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(
    R.layout.activity_mobile,
    RegisterViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
    }
}