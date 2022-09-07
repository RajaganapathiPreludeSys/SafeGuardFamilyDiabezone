package com.safeguardFamily.diabezone.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.safeguardFamily.diabezone.BuildConfig
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        mBinding.lifecycleOwner = this

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
//            mBinding.vvVideoPlayer.start()
            startActivity(Intent(applicationContext, MobileActivity::class.java))
//            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            finish()
        }
    }
}