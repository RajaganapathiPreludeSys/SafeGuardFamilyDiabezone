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
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.safeguardFamily.diabezone.BuildConfig
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityOnBoardingBinding
import com.safeguardFamily.diabezone.model.OnBoardingItem
import com.safeguardFamily.diabezone.ui.adapter.OnBoardingAdapter

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOnBoardingBinding
    private var onBoardingAdapter: OnBoardingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        mBinding.lifecycleOwner = this

        if (isInternetAvailable(this)) {
//            loadApp()
            loadViewPager()
        } else {
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
    }

    private fun loadViewPager() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPref.getUserId()!!.length > 2) {
                startActivity(Intent(applicationContext, DashboardActivity::class.java))
                finish()
            } else {
                mBinding.splash.visibility = View.GONE
                mBinding.rvOnBoarding.visibility = View.VISIBLE
                setOnBoardingItem()
                setOnBoardingIndicator()
                setCurrentOnBoardingIndicators(0)
                mBinding.vpOnBoarding.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentOnBoardingIndicators(position)
                    }
                })
                mBinding.btNext.setOnClickListener {
                    if (mBinding.vpOnBoarding.currentItem + 1 < onBoardingAdapter!!.itemCount) {
                        mBinding.vpOnBoarding.currentItem =
                            mBinding.vpOnBoarding.currentItem + 1
                    } else {
                        startActivity(Intent(applicationContext, MobileActivity::class.java))
                        finish()
                    }
                }
            }
        }, 2000)
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
            mBinding.rvProgress.visibility = View.VISIBLE
            Log.d("RRR -- ", "setOnPreparedListener: ")
            mBinding.vvVideoPlayer.visibility = View.VISIBLE
            mBinding.ivThumbnail.visibility = View.GONE

            val handler = Handler(Looper.getMainLooper())
            val progressStatus = 0
            mBinding.pbVideo.progress = 0
            val filesToDownload = mBinding.vvVideoPlayer.duration
            mBinding.pbVideo.max = filesToDownload

            val sec: Int = filesToDownload % 60
            val min: Int = filesToDownload / 60 % 60

            val strSec = if (sec < 10) "0$sec" else sec.toString()
            val strMin = if (min < 10) "0$min" else min.toString()

            mBinding.tvEnd.text = "$strMin:$strSec"
            Thread {
                while (progressStatus < filesToDownload) {
                    try {
                        Thread.sleep(10)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    handler.post {
                        mBinding.pbVideo.progress = mBinding.vvVideoPlayer.currentPosition
                    }
                }
            }.start()
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

    private fun setOnBoardingIndicator() {
        val indicators = arrayOfNulls<ImageView>(onBoardingAdapter!!.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_inactive
                )
            )
            indicators[i]!!.layoutParams = layoutParams
            mBinding.llIndicator.addView(indicators[i])
        }
    }

    private fun setCurrentOnBoardingIndicators(index: Int) {
        val childCount: Int = mBinding.llIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = mBinding.llIndicator.getChildAt(i) as ImageView
            if (i == index) imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_active
                )
            ) else imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_inactive
                )
            )
        }
        if (index == onBoardingAdapter!!.itemCount - 1) {
            mBinding.llIndicator.visibility = View.GONE
            mBinding.btNext.visibility = View.VISIBLE
            mBinding.btNext.text = "Get Started"
        } else {
            mBinding.llIndicator.visibility = View.VISIBLE
            mBinding.btNext.visibility = View.GONE
        }
    }

    private fun setOnBoardingItem() {
        val onBoardingItems: MutableList<OnBoardingItem> = ArrayList()
        val slide1 = OnBoardingItem()
        slide1.title = "Welcome to the Diabezone App"
        slide1.image =
            resources.getIdentifier("slide_1", "drawable", packageName)
        val slide2 = OnBoardingItem()
        slide2.title = "Track your diabetes log every day"
        slide2.image =
            resources.getIdentifier("slide_2", "drawable", packageName)
        val slide3 = OnBoardingItem()
        slide3.title = "Book an Appointment with the top Diabetes Doctors"
        slide3.image =
            resources.getIdentifier("slide_3", "drawable", packageName)
        val slide4 = OnBoardingItem()
        slide4.title = "Get Health Vault of all your medical records"
        slide4.image =
            resources.getIdentifier("slide_5", "drawable", packageName)
        val slide5 = OnBoardingItem()
        slide5.title = "Get a dedicated Health Coach, available 24x7"
        slide5.image =
            resources.getIdentifier("slide_6", "drawable", packageName)
        onBoardingItems.add(slide1)
        onBoardingItems.add(slide2)
        onBoardingItems.add(slide3)
        onBoardingItems.add(slide4)
        onBoardingItems.add(slide5)
        onBoardingAdapter = OnBoardingAdapter(onBoardingItems)
        mBinding.vpOnBoarding.adapter = onBoardingAdapter
    }
}