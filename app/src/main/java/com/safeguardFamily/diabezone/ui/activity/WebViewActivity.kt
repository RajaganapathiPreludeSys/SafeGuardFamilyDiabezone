package com.safeguardFamily.diabezone.ui.activity

import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.viewModel.WebViewViewModel
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.databinding.ActivityWebViewBinding

class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewViewModel>(
    R.layout.activity_web_view,
    WebViewViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
    }
}