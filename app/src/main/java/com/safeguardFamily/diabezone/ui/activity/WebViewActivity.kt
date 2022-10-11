package com.safeguardFamily.diabezone.ui.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.BuildConfig
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.databinding.ActivityWebViewBinding
import com.safeguardFamily.diabezone.viewModel.WebViewViewModel
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewViewModel>(
    R.layout.activity_web_view,
    WebViewViewModel::class.java
) {

    private var fileName = ""

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.ivBack.setOnClickListener { finish() }

        if (intent.extras?.containsKey(Bundle.KEY_WEB_KEY) == true
            && intent.extras?.containsKey(Bundle.KEY_WEB_URL) == true
        ) {
            mBinding.tvTitle.text = intent.extras?.getString(Bundle.KEY_WEB_KEY)
        }

        if (isInternetAvailable(this)) {
            var path = intent.extras?.getString(Bundle.KEY_WEB_URL).toString()
            if (intent.extras?.getString(Bundle.KEY_WEB_KEY) == "PDF") {
                mBinding.ivShare.visibility = View.VISIBLE
                mBinding.ivDownload.visibility = View.VISIBLE
                path =
                    "https://docs.google.com/gview?embedded=true&url=${
                        intent.extras?.getString(
                            Bundle.KEY_WEB_URL
                        )
                    }"
            }

            Log.d(TAG, "onceCreated: Path $path")

            mBinding.webView.webViewClient = WebViewClient()
            mBinding.webView.settings.javaScriptEnabled = true
            mBinding.webView.settings.setSupportZoom(true)
            mBinding.webView.settings.builtInZoomControls = true
            mBinding.webView.loadUrl(path)
        } else {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder
                .setMessage("No data/WIFI connection available. Please connect and retry")
                .setCancelable(false)
                .setPositiveButton("Retry") { dialog, id ->
                    var path = intent.extras?.getString(Bundle.KEY_WEB_URL).toString()
                    if (intent.extras?.getString(Bundle.KEY_WEB_KEY) == "PDF") {
                        mBinding.ivShare.visibility = View.VISIBLE
                        mBinding.ivDownload.visibility = View.VISIBLE
                        path =
                            "https://docs.google.com/gview?embedded=true&url=${
                                intent.extras?.getString(
                                    Bundle.KEY_WEB_URL
                                )
                            }"
                    }
                    Log.d(TAG, "onceCreated: Path $path")

                    mBinding.webView.webViewClient = WebViewClient()
                    mBinding.webView.settings.javaScriptEnabled = true
                    mBinding.webView.settings.setSupportZoom(true)
                    mBinding.webView.settings.builtInZoomControls = true
                    mBinding.webView.loadUrl(path)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    finish()
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Network State")
            alert.show()
        }

        mBinding.ivShare.setOnClickListener {
            if (fileName.length > 1) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    uriFromFile(
                        this,
                        File(this.getExternalFilesDir(null)?.absolutePath.toString(), fileName)
                    )
                )
                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                shareIntent.type = "application/pdf"
                startActivity(Intent.createChooser(shareIntent, "share.."))
            } else downloadPDFSend()
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Share PDF")
            }
        }


        mBinding.ivDownload.setOnClickListener {
            downloadPDF()
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Download PDF")
            }
        }
    }

    private fun uriFromFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID,
            file
        )
    }

    private fun downloadPDF() {
        try {
            val filepath = intent.extras?.getString(Bundle.KEY_WEB_URL)
            val url = URL(filepath)
            fileName = url.path.substring(fileName.lastIndexOf('/') + 1)
            if (fileName.contains("/"))
                fileName = url.path.substring(fileName.lastIndexOf('/') + 1)
            Log.d(TAG, "downloadPDF: $fileName")
            val request = DownloadManager.Request(Uri.parse(url.toString() + ""))
            request.setTitle(fileName)
            request.setMimeType("applcation/pdf")
            request.setAllowedOverMetered(true)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    private fun downloadPDFSend() {
        try {
            val filepath = intent.extras?.getString(Bundle.KEY_WEB_URL)
            val url = URL(filepath)
            fileName = url.path.substring(fileName.lastIndexOf('/') + 1)
            if (fileName.contains("/"))
                fileName = url.path.substring(fileName.lastIndexOf('/') + 1)
            Log.d(TAG, "downloadPDFSend: $fileName")
            val request = DownloadManager.Request(Uri.parse(url.toString() + ""))
            request.setTitle(fileName)
            request.setMimeType("applcation/pdf")
            request.setAllowedOverMetered(true)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)


            val outputFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + fileName
            )


            val intentShareFile = Intent(Intent.ACTION_SEND)
            val fileWithinMyDir: File = outputFile

            if (fileWithinMyDir.exists()) {
                intentShareFile.type = "application/pdf"
                val uri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID,
                    fileWithinMyDir
                )
                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
                intentShareFile.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                startActivity(Intent.createChooser(intentShareFile, "Share File"))
                startActivity(intentShareFile)
            }
            val chooser = Intent.createChooser(intentShareFile, "Share File")

            val resInfoList = this.packageManager.queryIntentActivities(
                chooser,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    uriFromFile(
                        this,
                        outputFile
                    ),
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            startActivity(chooser)

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (mBinding.webView.canGoBack())
            mBinding.webView.goBack()
        else super.onBackPressed()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }
}