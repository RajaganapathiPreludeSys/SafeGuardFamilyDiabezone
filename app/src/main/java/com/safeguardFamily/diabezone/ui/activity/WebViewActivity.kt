package com.safeguardFamily.diabezone.ui.activity

import android.webkit.WebViewClient
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.databinding.ActivityWebViewBinding
import com.safeguardFamily.diabezone.viewModel.WebViewViewModel

class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewViewModel>(
    R.layout.activity_web_view,
    WebViewViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.webView.webViewClient = WebViewClient()
        mBinding.webView.loadUrl("https://meet.google.com/zii-vyzx-zbo")
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.settings.setSupportZoom(true)
    }

    override fun onBackPressed() {
        if (mBinding.webView.canGoBack())
            mBinding.webView.goBack()
        else super.onBackPressed()
    }

}