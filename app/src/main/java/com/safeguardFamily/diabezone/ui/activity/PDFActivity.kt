package com.safeguardFamily.diabezone.ui.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.BuildConfig
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.databinding.ActivityPdfBinding
import com.safeguardFamily.diabezone.viewModel.PDFViewModel
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class PDFActivity : BaseActivity<ActivityPdfBinding, PDFViewModel>(
    R.layout.activity_pdf,
    PDFViewModel::class.java
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

        val path = intent.extras?.getString(Bundle.KEY_WEB_URL).toString()
        Log.d(TAG, "onceCreated: Path $path")
        if (isInternetAvailable(this)) mViewModel.loadPdf(cacheDir, path)
        else {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder
                .setMessage("No data/WIFI connection available. Please connect and retry")
                .setCancelable(false)
                .setPositiveButton("Retry") { dialog, id ->
                    mViewModel.loadPdf(cacheDir, path)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    finish()
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Network State")
            alert.show()
        }

        mBinding.ivShare.setOnClickListener {
//            val outputFile = File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + fileName
//            )
//            if (fileName.length > 1) {
//                val shareIntent = Intent(Intent.ACTION_SEND)
//                shareIntent.putExtra(
//                    Intent.EXTRA_STREAM,
//                    uriFromFile(
//                        this,
//                        File(this.getExternalFilesDir(null)?.absolutePath.toString(), fileName)
//                    )
//                )
//                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                shareIntent.type = "application/pdf"
//                startActivity(Intent.createChooser(shareIntent, "share.."))
//                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
//                    param(FirebaseAnalytics.Param.CONTENT, "Share PDF")
//                }
//            } else downloadPDFSend()

            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Regarding - Diabezone")
            i.putExtra(
                Intent.EXTRA_TEXT,
                "Seasonal Greetings from Diabezone\n\n\nPDF Link: $path"
            )
            startActivity(Intent.createChooser(i, "Share Via"))
        }

        mBinding.ivDownload.setOnClickListener {
            downloadPDF()
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Download PDF")
            }
        }

        mViewModel.pdfPath.observe(this) {
            mBinding.pdf.fromFile(it.toFile()).scale(1000f).show()
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
            val request: DownloadManager.Request =
                DownloadManager.Request(Uri.parse(url.toString() + ""))
            request.setTitle(fileName)
            request.setMimeType("applcation/pdf")
            request.setAllowedOverMetered(true)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
//            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//            dm.enqueue(request)
            beginDownload(request)
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

            if (outputFile.exists()) {
                intentShareFile.type = "application/pdf"
                val uri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID,
                    outputFile
                )
                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
                intentShareFile.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                startActivity(Intent.createChooser(intentShareFile, "Share File"))
//                startActivity(intentShareFile)
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

    private fun beginDownload(request: DownloadManager.Request) {

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.

        var finishDownload = false
        var progress: Int
        while (!finishDownload) {
            val cursor: Cursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_FAILED -> {
                        finishDownload = true
                    }
                    DownloadManager.STATUS_PAUSED -> {}
                    DownloadManager.STATUS_PENDING -> {}
                    DownloadManager.STATUS_RUNNING -> {
                        val total: Long =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (total >= 0) {
                            val downloaded: Long =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            progress = (downloaded * 100L / total).toInt()
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        progress = 100
                        finishDownload = true
                        val dialogBuilder = AlertDialog.Builder(this)
                        dialogBuilder
                            .setMessage("PDF Downloaded Completed")
                            .setCancelable(true)
                            .setPositiveButton("Ok") { dialog, id ->

                            }

                        val alert = dialogBuilder.create()
                        alert.setTitle("PDF Download")
                        alert.show()
                    }
                }
            }
        }
    }

}