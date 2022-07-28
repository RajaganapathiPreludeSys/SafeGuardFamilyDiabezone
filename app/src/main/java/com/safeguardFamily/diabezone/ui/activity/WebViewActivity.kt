package com.safeguardFamily.diabezone.ui.activity

import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.viewModel.WebViewViewModel
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.databinding.ActivityWebViewBinding

class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewViewModel>(
    R.layout.activity_web_view,
    WebViewViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
        mBinding.tvWelcome.text = intent.extras!!.getString(KEY_WEB_KEY)

        mBinding.ivBack.setOnClickListener { finish() }
    }

}