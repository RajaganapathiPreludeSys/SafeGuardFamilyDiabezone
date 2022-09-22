package com.safeguardFamily.diabezone.ui.activity

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.databinding.ActivityDoctorDetailsBinding
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.viewModel.DoctorDetailsViewModel


class DoctorDetailsActivity :
    BaseActivity<ActivityDoctorDetailsBinding, DoctorDetailsViewModel>(
        R.layout.activity_doctor_details,
        DoctorDetailsViewModel::class.java
    ) {

    lateinit var provider: Provider

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        if (intent.extras?.containsKey(Bundle.KEY_DOCTOR) == true) {
            provider = Gson()
                .fromJson(intent.extras?.getString(Bundle.KEY_DOCTOR), Provider::class.java)
            mBinding.provider = provider
        }

        if (intent.extras?.containsKey(Bundle.KEY_TITLE) == true) {
            mBinding.tvTitle.text = intent.extras?.getString(Bundle.KEY_TITLE)
            mBinding.llMakeAppointment.visibility = View.GONE
        } else mBinding.llContainer.visibility = View.GONE

        mBinding.llBookAppointment.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(provider))
            navigateTo(ScheduleAppointmentActivity::class.java, bundle, true)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Book appointment screen from Doctor Details ")
            }
        }

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.btChat.setOnClickListener { openWhatsApp(provider.mobile)

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Open Whatsapp chat from Doctor details screen")
            }}

        mBinding.btCall.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(provider))
            navigateTo(ScheduleAppointmentActivity::class.java, bundle, true)

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Call Doctor from Doctor details screen")
            }
        }
        loadAvailability()


        val spanString = SpannableString(provider.about + "...less")

        spanString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            provider.about.length,
            provider.about.length + 7,
            0
        )
        spanString.setSpan(
            ForegroundColorSpan(getColor(R.color.blue)),
            provider.about.length,
            provider.about.length + 7,
            0
        )
        mBinding.tvReadMore.setText(spanString, TextView.BufferType.SPANNABLE)

    }

    private fun loadAvailability() {
        if (provider.timings.days!!.mon!!.length > 1)
            mBinding.tvMonTime.text = provider.timings.days!!.mon
        else {
            mBinding.tvMonTime.text = "No slots available"
            mBinding.tvMon.setTextColor(getColor(R.color.red))
        }
        if (provider.timings.days!!.tue!!.length > 1)
            mBinding.tvTueTime.text = provider.timings.days!!.tue
        else {
            mBinding.tvTueTime.text = "No slots available"
            mBinding.tvTue.setTextColor(getColor(R.color.red))
        }
        if (provider.timings.days!!.wed!!.length > 1)
            mBinding.tvWedTime.text = provider.timings.days!!.wed
        else {
            mBinding.tvWedTime.text = "No slots available"
            mBinding.tvWed.setTextColor(getColor(R.color.red))
        }
        if (provider.timings.days!!.thu!!.length > 1)
            mBinding.tvThuTime.text = provider.timings.days!!.thu
        else {
            mBinding.tvThuTime.text = "No slots available"
            mBinding.tvThu.setTextColor(getColor(R.color.red))
        }
        if (provider.timings.days!!.fri!!.length > 1)
            mBinding.tvFriTime.text = provider.timings.days!!.fri
        else {
            mBinding.tvFriTime.text = "No slots available"
            mBinding.tvFri.setTextColor(getColor(R.color.red))
        }
        if (provider.timings.days!!.sat!!.length > 1)
            mBinding.tvSatTime.text = provider.timings.days!!.sat
        else {
            mBinding.tvSatTime.text = "No slots available"
            mBinding.tvSat.setTextColor(getColor(R.color.red))
        }
        if (provider.timings.days!!.sun!!.length > 1)
            mBinding.tvSunTime.text = provider.timings.days!!.sun
        else {
            mBinding.tvSunTime.text = "No slots available"
            mBinding.tvSun.setTextColor(getColor(R.color.red))
        }
    }
}