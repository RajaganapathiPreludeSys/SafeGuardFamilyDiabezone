package com.safeguardFamily.diabezone.ui.activity

import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.databinding.ActivitySubscriptionBinding
import com.safeguardFamily.diabezone.viewModel.SubscriptionViewModel

class SubscriptionActivity : BaseActivity<ActivitySubscriptionBinding, SubscriptionViewModel>(
    R.layout.activity_subscription,
    SubscriptionViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }

    }
}