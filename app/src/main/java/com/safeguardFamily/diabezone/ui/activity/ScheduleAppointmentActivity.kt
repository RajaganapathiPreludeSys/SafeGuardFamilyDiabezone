package com.safeguardFamily.diabezone.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.TimeAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.databinding.ActivityScheduleAppointmentBinding
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

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        val calendar = Calendar.getInstance()

        mBinding.calendar.minDate = calendar.timeInMillis
        val day = calendar[Calendar.DAY_OF_MONTH] + 30
        calendar.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH], day)
        mBinding.calendar.maxDate = calendar.timeInMillis

        mBinding.tvWelcome.text = calendar[Calendar.DAY_OF_MONTH].toString() + "-" +
                (calendar[Calendar.MONTH] + 1) + "-" + calendar[Calendar.YEAR]

        bsbTimeDialog = BottomSheetBehavior.from(mBinding.icBottomSheetTime.rlTimeDialog)
        bsbConfirmDialog = BottomSheetBehavior.from(mBinding.icBottomSheetConfirm.rlConfirmDialog)

        mBinding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            mBinding.tvWelcome.text = dayOfMonth.toString() + "-" + (month + 1) + "-" + year
            openTimeSelectorDialog(mBinding.tvWelcome.text as String)
            isTimeSelected = false

            bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
            bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN
        }
        bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
        bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN

        openTimeSelectorDialog(mBinding.tvWelcome.text as String)

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

        mBinding.btReschedule.setOnClickListener {  }

    }

    private fun openTimeSelectorDialog(text: String) {

        mBinding.icBottomSheetTime.tvSelectedDate.text = text
        val dataList = ArrayList<String>()
        dataList.add("8:00")
        dataList.add("8:30")
        dataList.add("9:00")
        dataList.add("2:00")
        dataList.add("2:30")
        dataList.add("18:00")
        dataList.add("18:30")
        dataList.add("19:00")
        dataList.add("20:00")
        dataList.add("20:30")
        mBinding.icBottomSheetTime.rvTimes.adapter = TimeAdapter(dataList)
        mBinding.icBottomSheetTime.rvTimes.layoutManager = GridLayoutManager(this, 3)
        mBinding.icBottomSheetTime.rvTimes.setHasFixedSize(true)
        mBinding.icBottomSheetTime.btMakeAppointment.setOnClickListener {
            bsbTimeDialog.state = BottomSheetBehavior.STATE_HIDDEN
            bsbConfirmDialog.state = BottomSheetBehavior.STATE_EXPANDED
            openConfirmDialog()
            isTimeSelected = true
        }
    }

    private fun openConfirmDialog() {

        mBinding.icBottomSheetConfirm.btConfirm.setOnClickListener {
            isConfirmed = true
            bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN
            mBinding.cvContainer.visibility = View.VISIBLE
        }
    }

}