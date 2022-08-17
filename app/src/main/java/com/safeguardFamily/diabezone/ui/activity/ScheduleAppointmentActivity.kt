package com.safeguardFamily.diabezone.ui.activity

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.TimeAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.common.DateUtils.apiDateFormat
import com.safeguardFamily.diabezone.common.DateUtils.formatDate
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.common.SharedPref.Pref.prefIsMember
import com.safeguardFamily.diabezone.databinding.ActivityScheduleAppointmentBinding
import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.viewModel.ScheduleAppointmentViewModel
import java.util.*

class ScheduleAppointmentActivity :
    BaseActivity<ActivityScheduleAppointmentBinding, ScheduleAppointmentViewModel>(
        R.layout.activity_schedule_appointment,
        ScheduleAppointmentViewModel::class.java
    ) {

    private lateinit var bsbTimeDialog: BottomSheetBehavior<View>
    private lateinit var bsbConfirmDialog: BottomSheetBehavior<View>
    private var isTimeSelected = false
    private var isConfirmed = false
    private var apiDate = ""
    private var apiTime = ""
    private lateinit var provider: Provider
    private var monthName = arrayOf(
        "jan", "feb", "mar", "apr", "may", "jun", "jul",
        "aug", "sep", "oct", "nov", "dec"
    )

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }
        mBinding.icHeader.tvTitle.text = getString(R.string.appointment)

        if (intent.extras?.containsKey(Bundle.KEY_DOCTOR) == true) {
            provider = Gson()
                .fromJson(intent.extras?.getString(Bundle.KEY_DOCTOR), Provider::class.java)
            mBinding.icBottomSheetConfirm.provider = provider
        }

        val cal = Calendar.getInstance()

        mBinding.calendar.minDate = cal.timeInMillis
        getAvailableSlots(cal)
        var daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH])
        daysInMonth += cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(cal[Calendar.YEAR], cal[Calendar.MONTH] - 1, daysInMonth)
        mBinding.calendar.maxDate = cal.timeInMillis

        bsbTimeDialog = BottomSheetBehavior.from(mBinding.icBottomSheetTime.rlTimeDialog)
        bsbConfirmDialog = BottomSheetBehavior.from(mBinding.icBottomSheetConfirm.rlConfirmDialog)

        mBinding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->

            cal.set(year, month, dayOfMonth)
            getAvailableSlots(cal)

            isTimeSelected = false

            bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
            bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN

        }

        bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
        bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN

        bsbTimeDialog.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!isTimeSelected) bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        bsbConfirmDialog.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (isTimeSelected && bsbTimeDialog.state != BottomSheetBehavior.STATE_EXPANDED && !isConfirmed)
                    bsbConfirmDialog.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        mBinding.btReschedule.setOnClickListener {
            isTimeSelected = false

            bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
            bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun getAvailableSlots(calendar: Calendar) {
        apiDate = apiDateFormat(calendar.timeInMillis)
        mBinding.icBottomSheetConfirm.date = formatDate(calendar.timeInMillis)
        mBinding.icBottomSheetTime.tvSelectedDate.text = formatDate(calendar.timeInMillis)
        provider.available_slots.forEach { it ->
            if (it.month.startsWith(monthName[calendar[Calendar.MONTH]], true)) {
                it.days.forEach {
                    if (it.date == calendar[Calendar.DAY_OF_MONTH]) {
                        openTimeSelectorDialog(it.slots)
                    }
                }
            }
        }
    }

    private fun openTimeSelectorDialog(slots: List<String>) {
        apiTime = ""
        if (slots.isNotEmpty()) {
            mBinding.icBottomSheetTime.rvTimes.visibility = View.VISIBLE
            mBinding.icBottomSheetTime.tvTimes.visibility = View.GONE
        } else {
            mBinding.icBottomSheetTime.rvTimes.visibility = View.GONE
            mBinding.icBottomSheetTime.tvTimes.visibility = View.VISIBLE
        }
        mBinding.icBottomSheetTime.rvTimes.adapter = TimeAdapter(slots) {
            apiTime = it
            mBinding.icBottomSheetConfirm.time = DateUtils.formatTo12Hrs(it)!!.uppercase()
        }
        mBinding.icBottomSheetTime.rvTimes.layoutManager = GridLayoutManager(this, 3)
        mBinding.icBottomSheetTime.rvTimes.setHasFixedSize(true)
        mBinding.icBottomSheetTime.btMakeAppointment.setOnClickListener {
            if (apiTime.length > 1) {
                bsbTimeDialog.state = BottomSheetBehavior.STATE_HIDDEN
                bsbConfirmDialog.state = BottomSheetBehavior.STATE_EXPANDED
                openConfirmDialog()
                isTimeSelected = true
                Log.d(TAG, " calendar = $apiDate + $apiTime")
            } else showToast("Time slot not selected/available")
        }
    }

    private fun openConfirmDialog() {

        mBinding.icBottomSheetConfirm.isMember = SharedPref.read(prefIsMember, false)
        mBinding.icBottomSheetConfirm.btConfirm.setOnClickListener {
            isConfirmed = true
//            bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN
//            mBinding.cvContainer.visibility = View.VISIBLE
            val request = CreateAppointmentRequest(
                puid = provider.puid.toString(),
                sel_date = apiDate,
                slot = apiTime
            )
            mViewModel.createAppointment(request)
        }
    }

}