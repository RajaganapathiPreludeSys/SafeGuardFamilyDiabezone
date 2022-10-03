package com.safeguardFamily.diabezone.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.ui.adapter.TimeAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.common.DateUtils.apiDateFormat
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFromTimeStamp
import com.safeguardFamily.diabezone.common.DateUtils.getTimeStampFromSting
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityScheduleAppointmentBinding
import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.request.GetSlotsRequest
import com.safeguardFamily.diabezone.model.response.Appointment
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.viewModel.ScheduleAppointmentViewModel
import java.util.*

class ScheduleAppointmentActivity :
    BaseActivity<ActivityScheduleAppointmentBinding, ScheduleAppointmentViewModel>(
        R.layout.activity_schedule_appointment,
        ScheduleAppointmentViewModel::class.java
    ) {

    private var isReschedule = false
    private var apiDate = ""
    private var apiTime = ""
    private lateinit var mProvider: Provider
    private lateinit var mAppointment: Appointment
    private var monthName = arrayOf(
        "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"
    )
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.ivBack.setOnClickListener { finish() }

        if (intent.extras?.containsKey(Bundle.KEY_DOCTOR) == true) {
            mProvider = Gson()
                .fromJson(intent.extras?.getString(Bundle.KEY_DOCTOR), Provider::class.java)
            mBinding.btMakeAppointment.text =
                getString(R.string.make_an_appointment)
        } else if (intent.extras?.containsKey(Bundle.KEY_APPOINTMENT) == true) {
            isReschedule = true
            mAppointment = Gson()
                .fromJson(intent.extras?.getString(Bundle.KEY_APPOINTMENT), Appointment::class.java)
            mProvider = mAppointment.provider
            mBinding.btMakeAppointment.text =
                getString(R.string.reschedule_an_appointment)
        }
        mBinding.provider = mProvider

        if (mProvider.available_slots == null || mProvider.available_slots?.isEmpty() == true) {
            mViewModel.getSlots(
                GetSlotsRequest(
                    puid = mProvider.puid,
                    uid = SharedPref.getUserId()!!
                )
            ) { slots ->
                mProvider.available_slots = slots
                loadCalendar()
            }
        }

        loadCalendar()

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == 123 && result.data != null) finish()
            }
    }

    private fun loadCalendar() {
        val cal = Calendar.getInstance()

        mBinding.calendar.minDate = cal.timeInMillis
        getAvailableSlots(cal)
        if (isReschedule) {
            val c = Calendar.getInstance()
            c.timeInMillis = getTimeStampFromSting(mAppointment.booking_date)
            mBinding.calendar.date = getTimeStampFromSting(mAppointment.booking_date)
            getAvailableSlots(c, mAppointment.slot)
        }

        var daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH])
        daysInMonth += cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(cal[Calendar.YEAR], cal[Calendar.MONTH] - 1, daysInMonth)
        mBinding.calendar.maxDate = cal.timeInMillis

        mBinding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth)
            getAvailableSlots(cal)
        }
    }

    private fun getAvailableSlots(calendar: Calendar, tempSlot: String? = "") {
        apiDate = apiDateFormat(calendar.timeInMillis)
        mBinding.date = displayingDateFromTimeStamp(calendar.timeInMillis)
        mBinding.tvSelectedDate.text =
            displayingDateFromTimeStamp(calendar.timeInMillis)
        mProvider.available_slots?.forEach { slot ->
            if (slot.month.startsWith(monthName[calendar[Calendar.MONTH]], true)) {
                slot.days.forEach { day ->
                    if (day.date == calendar[Calendar.DAY_OF_MONTH])
                        openTimeSelectorDialog(day.slots as ArrayList<String>, tempSlot)
                }
            }
        }
    }

    private fun openTimeSelectorDialog(slots: ArrayList<String>, tempSlot: String? = "") {
        apiTime = ""
        if (slots.isNotEmpty()) {
            mBinding.rvTimes.visibility = View.VISIBLE
            mBinding.tvTimes.visibility = View.GONE
        } else {
            mBinding.rvTimes.visibility = View.GONE
            mBinding.tvTimes.visibility = View.VISIBLE
        }
        mBinding.tvSelectedSlot.text =
            if (tempSlot?.length!! > 1) "Selected slot - ${
                DateUtils.formatTo12Hrs(tempSlot)!!.uppercase()
            }" else ""
        mBinding.rvTimes.adapter = TimeAdapter(slots) {
            apiTime = it
            mBinding.time = DateUtils.formatTo12Hrs(it)!!.uppercase()
        }
        mBinding.rvTimes.layoutManager = GridLayoutManager(this, 3)
        mBinding.rvTimes.setHasFixedSize(true)
        mBinding.btMakeAppointment.setOnClickListener {
            if (apiTime.length > 1) {
                Log.d(TAG, " calendar = $apiDate $apiTime")
                val bundle = android.os.Bundle()
                bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(mProvider))
                if (isReschedule)
                    bundle.putString(Bundle.KEY_RESCHEDULE, mAppointment.aid)
                bundle.putString(
                    Bundle.KEY_CREATE_APPOINTMENT, Gson().toJson(
                        CreateAppointmentRequest(
                            uid = SharedPref.getUserId()!!,
                            puid = mProvider.puid,
                            sel_date = apiDate,
                            slot = apiTime,
                        )
                    )
                )

                activityResultLauncher.launch(
                    Intent(
                        this,
                        AppointmentPaymentActivity::class.java
                    ).putExtras(bundle)
                )
            } else showToast("Time slot not selected/available")

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Appointment Date and Time selected and move to Appointment Payment screen")
            }
        }
    }
}