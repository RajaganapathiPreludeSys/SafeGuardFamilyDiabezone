package com.safeguardFamily.diabezone.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        mBinding.lifecycleOwner = this

        mBinding.btSkip.setOnClickListener {
            startActivity(Intent(applicationContext, MobileActivity::class.java))
            finish()
        }
        loadVideoView()
    }


    private fun loadVideoView() {
        val url =
            "https://media.geeksforgeeks.org/wp-content/uploads/20210629133649/build-a-simple-Calculator-app-using-Android-Studio.mp4"
        val filePath = "https://safeguardfamily.com/videos/diabetes-program.mp4"
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