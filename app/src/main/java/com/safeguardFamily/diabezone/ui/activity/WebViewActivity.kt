package com.safeguardFamily.diabezone.ui.activity

import android.util.Log
import android.webkit.WebViewClient
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
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

        var path = intent.extras?.getString(Bundle.KEY_WEB_URL).toString()
        if (intent.extras?.getString(Bundle.KEY_WEB_KEY) == "PDF")
            path =
                "https://docs.google.com/gview?embedded=true&url=${intent.extras?.getString(Bundle.KEY_WEB_URL)}"

        Log.d(TAG, "onceCreated: Path $path")

        mBinding.webView.webViewClient = WebViewClient()
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.settings.setSupportZoom(true)
        mBinding.webView.loadUrl(path)

    }

    override fun onBackPressed() {
        if (mBinding.webView.canGoBack())
            mBinding.webView.goBack()
        else super.onBackPressed()
    }

}