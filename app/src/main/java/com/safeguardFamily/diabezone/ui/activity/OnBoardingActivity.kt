package com.safeguardFamily.diabezone.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.BuildConfig
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityOnBoardingBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOnBoardingBinding
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        mBinding.lifecycleOwner = this

        if (isInternetAvailable(this))
            loadApp()
        else {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder
                .setMessage("No data/WIFI connection available. Please connect and retry")
                .setCancelable(false)
                .setPositiveButton("Retry") { dialog, id ->
                    finish()
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    finishAffinity()
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Network State")
            alert.show()
        }
        analytics = Firebase.analytics
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "id")
            param(FirebaseAnalytics.Param.ITEM_NAME, "name")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
            param(FirebaseAnalytics.Param.SCREEN_NAME, this.javaClass.toString())
        }

    }

    private fun loadApp() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPref.getUserId()!!.length > 2) {
                startActivity(Intent(applicationContext, DashboardActivity::class.java))
                finish()
            } else {
                mBinding.splash.visibility = View.GONE
                mBinding.video.visibility = View.VISIBLE
                loadVideoView()
                if (BuildConfig.BUILD_TYPE == "debug") {
                    mBinding.btSkip.visibility = View.VISIBLE
                    mBinding.btSkip.setOnClickListener {
                        startActivity(Intent(applicationContext, MobileActivity::class.java))
                        finish()
                    }
                }
            }
        }, 2000)
    }


    private fun loadVideoView() {
        val url =
            "https://media.geeksforgeeks.org/wp-content/uploads/20210629133649/build-a-simple-Calculator-app-using-Android-Studio.mp4"
        val filePath = "https://safeguardfamily.com/videos/diabetes-intro.mp4"
        val uri = Uri.parse(filePath)
        mBinding.vvVideoPlayer.setVideoURI(uri)
        mBinding.vvVideoPlayer.start()

        mBinding.ivThumbnail.visibility = View.VISIBLE
        mBinding.vvVideoPlayer.setOnPreparedListener {
            Log.d("RRR -- ", "setOnPreparedListener: ")
            mBinding.vvVideoPlayer.visibility = View.VISIBLE
            mBinding.ivThumbnail.visibility = View.GONE
        }

        mBinding.vvVideoPlayer.setOnCompletionListener {
            Log.d("RRR -- ", "restarted: ")
            startActivity(Intent(applicationContext, MobileActivity::class.java))
//            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            finish()
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
}