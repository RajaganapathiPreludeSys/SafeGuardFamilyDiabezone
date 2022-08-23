package com.safeguardFamily.diabezone.ui.activity

import android.webkit.WebViewClient
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.databinding.ActivityWebViewBinding
import com.safeguardFamily.diabezone.viewModel.WebViewViewModel

class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewViewModel>(
    R.layout.activity_web_view,
    WebViewViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }

        if (intent.extras?.containsKey(Bundle.KEY_WEB_KEY) == true
            && intent.extras?.containsKey(Bundle.KEY_WEB_URL) == true
        ) {
            mBinding.icHeader.tvTitle.text = intent.extras?.getString(Bundle.KEY_WEB_KEY)
        }

        mBinding.webView.webViewClient = WebViewClient()
        mBinding.webView.loadUrl(intent.extras?.getString(Bundle.KEY_WEB_URL).toString())
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.settings.setSupportZoom(true)
    }

    override fun onBackPressed() {
        if (mBinding.webView.canGoBack())
            mBinding.webView.goBack()
        else super.onBackPressed()
    }

}