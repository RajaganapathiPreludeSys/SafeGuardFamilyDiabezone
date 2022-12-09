package com.safeguardFamily.diabezone.ui.activity

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import java.text.DateFormatSymbols
import java.util.*

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
        } else {
            mBinding.tvTitle.text = if (countWords(provider.category) > 1) provider.category
            else "${provider.category}'s Details"
            mBinding.llContainer.visibility = View.GONE
        }

        if (provider.type == "doctor") {
            mBinding.tvExpValue.text = provider.experience
            mBinding.tvExperience.text = "Experience"
        } else {
            mBinding.tvExpValue.text = provider.num_consultations
            mBinding.tvExperience.text = "Consultations"
        }

        mBinding.llBookAppointment.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(provider))
            navigateTo(ScheduleAppointmentActivity::class.java, bundle, true)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(
                    FirebaseAnalytics.Param.CONTENT,
                    "Go to Book appointment screen from Doctor Details "
                )
            }
        }

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.btChat.setOnClickListener {
            openWhatsApp(provider.mobile)

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(
                    FirebaseAnalytics.Param.CONTENT,
                    "Open Whatsapp chat from Doctor details screen"
                )
            }
        }

        mBinding.btCall.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(provider))
            navigateTo(ScheduleAppointmentActivity::class.java, bundle, true)

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Call Doctor from Doctor details screen")
            }
        }
        loadAvailability()


        val spanString = SpannableString(provider.about + "..less")

        spanString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            provider.about.length,
            provider.about.length + 6,
            0
        )
        spanString.setSpan(
            ForegroundColorSpan(getColor(R.color.blue)),
            provider.about.length,
            provider.about.length + 6,
            0
        )
        mBinding.tvReadMore.setText(spanString, TextView.BufferType.SPANNABLE)

    }

    private fun countWords(inputString: String): Int {
        val strArray = inputString.split(" ".toRegex()).toTypedArray()
        var count = 0
        for (s in strArray) if (s != "") count++
        return count
    }

    private fun loadAvailability() {
        resetBg()

        mBinding.tvMonTime.setOnClickListener {
            resetBg()
            mBinding.tvMonTime.setBackgroundResource(R.drawable.bg_green_circle)
            mBinding.tvTimeValue.text = if (provider.timings.days!!.mon!!.length < 2)
                "No slots available for this day" else provider.timings.days!!.mon
        }

        mBinding.tvTueTime.setOnClickListener {
            resetBg()
            mBinding.tvTueTime.setBackgroundResource(R.drawable.bg_green_circle)
            mBinding.tvTimeValue.text = if (provider.timings.days!!.tue!!.length < 2)
                "No slots available for this day" else provider.timings.days!!.tue
        }

        mBinding.tvWedTime.setOnClickListener {
            resetBg()
            mBinding.tvWedTime.setBackgroundResource(R.drawable.bg_green_circle)
            mBinding.tvTimeValue.text = if (provider.timings.days!!.wed!!.length < 2)
                "No slots available for this day" else provider.timings.days!!.wed
        }

        mBinding.tvThuTime.setOnClickListener {
            resetBg()
            mBinding.tvThuTime.setBackgroundResource(R.drawable.bg_green_circle)
            mBinding.tvTimeValue.text = if (provider.timings.days!!.thu!!.length < 2)
                "No slots available for this day" else provider.timings.days!!.thu
        }

        mBinding.tvFriTime.setOnClickListener {
            resetBg()
            mBinding.tvFriTime.setBackgroundResource(R.drawable.bg_green_circle)
            mBinding.tvTimeValue.text = if (provider.timings.days!!.fri!!.length < 2)
                "No slots available for this day" else provider.timings.days!!.fri
        }

        mBinding.tvSatTime.setOnClickListener {
            resetBg()
            mBinding.tvSatTime.setBackgroundResource(R.drawable.bg_green_circle)
            mBinding.tvTimeValue.text = if (provider.timings.days!!.sat!!.length < 2)
                "No slots available for this day" else provider.timings.days!!.sat
        }

        mBinding.tvSunTime.setOnClickListener {
            resetBg()
            mBinding.tvSunTime.setBackgroundResource(R.drawable.bg_green_circle)
            mBinding.tvTimeValue.text = if (provider.timings.days!!.sun!!.length < 2)
                "No slots available for this day" else provider.timings.days!!.sun
        }

        val date = Calendar.getInstance()
        when (DateFormatSymbols().weekdays[date[Calendar.DAY_OF_WEEK]]) {
            "Sunday" -> {
                mBinding.tvTimeValue.text = if (provider.timings.days!!.sun!!.length < 2)
                    "No slots available for this day" else provider.timings.days!!.sun
                mBinding.tvSunTime.setBackgroundResource(R.drawable.bg_green_circle)
            }
            "Monday" -> {
                mBinding.tvTimeValue.text = if (provider.timings.days!!.mon!!.length < 2)
                    "No slots available for this day" else provider.timings.days!!.mon
                mBinding.tvMonTime.setBackgroundResource(R.drawable.bg_green_circle)
            }
            "Tuesday" -> {
                mBinding.tvTimeValue.text = if (provider.timings.days!!.tue!!.length < 2)
                    "No slots available for this day" else provider.timings.days!!.tue
                mBinding.tvTueTime.setBackgroundResource(R.drawable.bg_green_circle)
            }
            "Wednesday" -> {
                mBinding.tvTimeValue.text = if (provider.timings.days!!.wed!!.length < 2)
                    "No slots available for this day" else provider.timings.days!!.wed
                mBinding.tvWedTime.setBackgroundResource(R.drawable.bg_green_circle)
            }
            "Thursday" -> {
                mBinding.tvTimeValue.text = if (provider.timings.days!!.thu!!.length < 2)
                    "No slots available for this day" else provider.timings.days!!.thu
                mBinding.tvThuTime.setBackgroundResource(R.drawable.bg_green_circle)
            }
            "Friday" -> {
                mBinding.tvTimeValue.text = if (provider.timings.days!!.fri!!.length < 2)
                    "No slots available for this day" else provider.timings.days!!.fri
                mBinding.tvFriTime.setBackgroundResource(R.drawable.bg_green_circle)
            }
            "Saturday" -> {
                mBinding.tvTimeValue.text = if (provider.timings.days!!.sat!!.length < 2)
                    "No slots available for this day" else provider.timings.days!!.sat
                mBinding.tvSatTime.setBackgroundResource(R.drawable.bg_green_circle)
            }
        }
    }

    private fun resetBg() {
        if (provider.timings.days!!.mon!!.length < 2)
            mBinding.tvMonTime.setBackgroundResource(R.drawable.bg_red_circle)
        else mBinding.tvMonTime.setBackgroundResource(R.drawable.bg_royal_blue_circle)

        if (provider.timings.days!!.tue!!.length < 2)
            mBinding.tvTueTime.setBackgroundResource(R.drawable.bg_red_circle)
        else mBinding.tvTueTime.setBackgroundResource(R.drawable.bg_royal_blue_circle)

        if (provider.timings.days!!.wed!!.length < 2)
            mBinding.tvWedTime.setBackgroundResource(R.drawable.bg_red_circle)
        else mBinding.tvWedTime.setBackgroundResource(R.drawable.bg_royal_blue_circle)

        if (provider.timings.days!!.thu!!.length < 2)
            mBinding.tvThuTime.setBackgroundResource(R.drawable.bg_red_circle)
        else mBinding.tvThuTime.setBackgroundResource(R.drawable.bg_royal_blue_circle)

        if (provider.timings.days!!.fri!!.length < 2)
            mBinding.tvFriTime.setBackgroundResource(R.drawable.bg_red_circle)
        else mBinding.tvFriTime.setBackgroundResource(R.drawable.bg_royal_blue_circle)

        if (provider.timings.days!!.sat!!.length < 2)
            mBinding.tvSatTime.setBackgroundResource(R.drawable.bg_red_circle)
        else mBinding.tvSatTime.setBackgroundResource(R.drawable.bg_royal_blue_circle)

        if (provider.timings.days!!.sun!!.length < 2)
            mBinding.tvSunTime.setBackgroundResource(R.drawable.bg_red_circle)
        else mBinding.tvSunTime.setBackgroundResource(R.drawable.bg_royal_blue_circle)
    }
}