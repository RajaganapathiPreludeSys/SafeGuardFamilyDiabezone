package com.safeguardFamily.diabezone.ui.activity

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.TimeAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.common.DateUtils.apiDateFormat
import com.safeguardFamily.diabezone.common.DateUtils.formatDate
import com.safeguardFamily.diabezone.common.DateUtils.getTimeStampFromSting
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.common.SharedPref.Pref.prefIsMember
import com.safeguardFamily.diabezone.databinding.ActivityScheduleAppointmentBinding
import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.request.GetSlotsRequest
import com.safeguardFamily.diabezone.model.response.Appointment
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.viewModel.ScheduleAppointmentViewModel
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ScheduleAppointmentActivity :
    BaseActivity<ActivityScheduleAppointmentBinding, ScheduleAppointmentViewModel>(
        R.layout.activity_schedule_appointment,
        ScheduleAppointmentViewModel::class.java
    ), PaymentResultListener {

    private lateinit var bsbTimeDialog: BottomSheetBehavior<View>
    private lateinit var bsbConfirmDialog: BottomSheetBehavior<View>
    private var isTimeSelected = false
    private var isConfirmed = false
    private var isReschedule = false
    private var apiDate = ""
    private var apiTime = ""
    private lateinit var mProvider: Provider
    private lateinit var mAppointment: Appointment
    private var monthName = arrayOf(
        "jan", "feb", "mar", "apr", "may", "jun", "jul",
        "aug", "sep", "oct", "nov", "dec"
    )

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }
        mBinding.icHeader.tvTitle.text = getString(R.string.appointment)

        if (intent.extras?.containsKey(Bundle.KEY_DOCTOR) == true) {
            mProvider = Gson()
                .fromJson(intent.extras?.getString(Bundle.KEY_DOCTOR), Provider::class.java)
            mBinding.icBottomSheetTime.btMakeAppointment.text =
                getString(R.string.make_an_appointment)
        } else if (intent.extras?.containsKey(Bundle.KEY_APPOINTMENT) == true) {
            isReschedule = true
            mAppointment = Gson()
                .fromJson(intent.extras?.getString(Bundle.KEY_APPOINTMENT), Appointment::class.java)
            mProvider = mAppointment.provider
            mBinding.icBottomSheetTime.btMakeAppointment.text =
                getString(R.string.reschedule_an_appointment)
        }
        mBinding.icBottomSheetConfirm.provider = mProvider
        mBinding.provider = mProvider

        mBinding.btReschedule.setOnClickListener {
            isTimeSelected = false
            isConfirmed = false

            isReschedule = true
            mBinding.cvContainer.visibility = View.GONE

            mAppointment = mViewModel.appointment.value!!.appointment
            Log.d(TAG, "mAppointment " + Gson().toJson(mAppointment))

            mProvider = mAppointment.provider
            if (mProvider.available_slots == null || mProvider.available_slots?.isEmpty() == true) {
                mViewModel.getSlots(GetSlotsRequest(puid = mProvider.puid)) { slots ->
                    mProvider.available_slots = slots
                    loadCalendar()
                }
            }

            bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
            bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN

        }

        mBinding.btJoinOnline.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(mProvider.vchat_url)
                )
            )
        }

        mViewModel.isBookingCompleted.observe(this) {
            if (it) {
                bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN
                mBinding.cvContainer.visibility = View.VISIBLE
            }
        }

        if (mProvider.available_slots == null || mProvider.available_slots?.isEmpty() == true) {
            mViewModel.getSlots(GetSlotsRequest(puid = mProvider.puid)) { slots ->
                mProvider.available_slots = slots
                loadCalendar()
            }
        }

        loadCalendar()
        loadBottomDialogs()
    }

    private fun loadBottomDialogs() {
        bsbTimeDialog = BottomSheetBehavior.from(mBinding.icBottomSheetTime.rlTimeDialog)
        bsbConfirmDialog = BottomSheetBehavior.from(mBinding.icBottomSheetConfirm.rlConfirmDialog)

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

            isTimeSelected = false

            bsbTimeDialog.state = BottomSheetBehavior.STATE_EXPANDED
            bsbConfirmDialog.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun getAvailableSlots(calendar: Calendar, tempSlot: String? = "") {
        apiDate = apiDateFormat(calendar.timeInMillis)
        mBinding.icBottomSheetConfirm.date = formatDate(calendar.timeInMillis)
        mBinding.icBottomSheetTime.tvSelectedDate.text = formatDate(calendar.timeInMillis)
        mProvider.available_slots?.forEach { slot ->
            if (slot.month.startsWith(monthName[calendar[Calendar.MONTH]], true)) {
                slot.days.forEach { day ->
                    if (day.date == calendar[Calendar.DAY_OF_MONTH]) {
                        openTimeSelectorDialog(day.slots, tempSlot)
                    }
                }
            }
        }
    }

    private fun openTimeSelectorDialog(slots: List<String>, tempSlot: String? = "") {
        apiTime = ""
        if (slots.isNotEmpty()) {
            mBinding.icBottomSheetTime.rvTimes.visibility = View.VISIBLE
            mBinding.icBottomSheetTime.tvTimes.visibility = View.GONE
        } else {
            mBinding.icBottomSheetTime.rvTimes.visibility = View.GONE
            mBinding.icBottomSheetTime.tvTimes.visibility = View.VISIBLE
        }
        mBinding.icBottomSheetTime.rvTimes.adapter = TimeAdapter(slots, tempSlot) {
            apiTime = it
            mBinding.icBottomSheetConfirm.time = DateUtils.formatTo12Hrs(it)!!.uppercase()
        }
        mBinding.icBottomSheetTime.rvTimes.layoutManager = GridLayoutManager(this, 3)
        mBinding.icBottomSheetTime.rvTimes.setHasFixedSize(true)
        mBinding.icBottomSheetTime.btMakeAppointment.setOnClickListener {
            if (isReschedule) {
                if (mAppointment.booking_date.contains(apiDate)
                    && mAppointment.slot.contains(apiTime)
                ) {
                    showToast("Please select a new date and time for rescheduling")
                    return@setOnClickListener
                }
            }
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
        mBinding.date = DateUtils.displayingDateFormat(apiDate)
        mBinding.time = DateUtils.formatTo12Hrs(apiTime)!!.uppercase()
        mBinding.icBottomSheetConfirm.isMember = SharedPref.read(prefIsMember, false)
        mBinding.icBottomSheetConfirm.btConfirm.setOnClickListener {
            isConfirmed = true
            if (SharedPref.read(prefIsMember, false)) {
                if (isReschedule) mViewModel.reScheduleAppointment(
                    CreateAppointmentRequest(
                        puid = mProvider.puid,
                        sel_date = apiDate,
                        slot = apiTime,
                        aid = mAppointment.aid
                    )
                )
                else mViewModel.createAppointment(
                    CreateAppointmentRequest(
                        puid = mProvider.puid,
                        sel_date = apiDate,
                        slot = apiTime
                    )
                )
            } else {
                if (isReschedule) mViewModel.reScheduleAppointment(
                    CreateAppointmentRequest(
                        puid = mProvider.puid,
                        sel_date = apiDate,
                        slot = apiTime,
                        aid = mAppointment.aid
                    )
                )
                else {
                    val amount = 800 * 100
                    val checkout = Checkout()
                    checkout.setKeyID("rzp_test_C5aketpmxb6Hl6")
                    checkout.setImage(R.drawable.ic_app_logo)
                    val obj = JSONObject()
                    try {
                        obj.put("name", "SafeGuardFamily")
                        obj.put("description", "Payment for appointment")
                        obj.put("theme.color", "")
                        obj.put("currency", "INR")
                        obj.put("amount", amount)
                        obj.put("prefill.contact", "9003440134")
                        obj.put("prefill.email", "test@mail.com")
                        checkout.open(this, obj)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        showToast("Payment succeeded")
        Log.d(TAG, "onPayment Success() called with: p0 = $p0")
        isConfirmed = true
        mViewModel.createAppointment(
            CreateAppointmentRequest(
                puid = mProvider.puid,
                sel_date = apiDate,
                slot = apiTime
            )
        )
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        showToast("Payment failed")
        Log.d(TAG, "onPayment Error() called with: p0 = $p0, p1 = $p1")
    }

}