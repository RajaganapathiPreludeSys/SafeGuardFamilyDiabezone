package com.safeguardFamily.diabezone.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityAppointmentPaymentBinding
import com.safeguardFamily.diabezone.model.request.CreateAppointmentRequest
import com.safeguardFamily.diabezone.model.request.Error
import com.safeguardFamily.diabezone.model.request.PaymentFailRequest
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.viewModel.AppointmentPaymentViewModel
import org.json.JSONException
import org.json.JSONObject


class AppointmentPaymentActivity :
    BaseActivity<ActivityAppointmentPaymentBinding, AppointmentPaymentViewModel>(
        R.layout.activity_appointment_payment,
        AppointmentPaymentViewModel::class.java
    ), PaymentResultListener {

    private var apiDate = ""
    private var apiTime = ""
    private lateinit var mProvider: Provider
    private lateinit var mRequest: CreateAppointmentRequest
    private var appointmentID = ""

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }
        mBinding.icHeader.tvTitle.text = "Appointment"

        if (intent.extras?.containsKey(Bundle.KEY_DOCTOR) == true
            && intent.extras?.containsKey(Bundle.KEY_CREATE_APPOINTMENT) == true
        ) {
            mProvider =
                Gson().fromJson(intent.extras?.getString(Bundle.KEY_DOCTOR), Provider::class.java)
            mRequest = Gson().fromJson(
                intent.extras?.getString(Bundle.KEY_CREATE_APPOINTMENT),
                CreateAppointmentRequest::class.java
            )

            apiDate = mRequest.sel_date
            apiTime = mRequest.slot

            mBinding.provider = mProvider
            mBinding.date = DateUtils.displayingDateFormat(mRequest.sel_date)
            mBinding.time = DateUtils.formatTo12Hrs(mRequest.slot)!!.uppercase()
            if (intent.extras?.containsKey(Bundle.KEY_RESCHEDULE) == true)
                appointmentID = intent.extras?.getString(Bundle.KEY_RESCHEDULE)!!
        }

        if (mProvider.isFree) {
            mBinding.tvPayStatus.text = "Confirm Now"
            mBinding.tvAmount.text = "₹0"
            mBinding.tvTitle.text = "Confirm Appointment"
            mBinding.tvFreeDesc.visibility = View.VISIBLE
        } else {
            mBinding.tvPayStatus.text = "Pay Online"
            mBinding.tvAmount.text = "₹${mProvider.fees}"
            mBinding.tvTitle.text = "Pay Online"
            mBinding.tvFreeDesc.visibility = View.GONE
        }

        if (appointmentID.isNotEmpty()) {
            mBinding.tvPayStatus.text = "Reschedule Now"
            mBinding.tvAmount.text = "₹0"
            mBinding.tvTitle.text = "Already Paid"
        }

        mBinding.ivEdit.setOnClickListener { finish()

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Edit appointment")
            }}

        mBinding.ivEdit.bringToFront()

        mBinding.llPaymentProcess.setOnClickListener {
            if (appointmentID.isNotEmpty()) mViewModel.reScheduleAppointment(
                CreateAppointmentRequest(
                    uid = SharedPref.getUserId()!!,
                    puid = mProvider.puid,
                    sel_date = apiDate,
                    slot = apiTime,
                    aid = appointmentID
                )
            )
            else mViewModel.createAppointment(
                CreateAppointmentRequest(
                    uid = SharedPref.getUserId()!!,
                    puid = mProvider.puid,
                    sel_date = apiDate,
                    slot = apiTime
                )
            ) {
                val amount = mProvider.fees.toInt() * 100
                val checkout = Checkout()
                checkout.setKeyID("rzp_test_C5aketpmxb6Hl6")
                checkout.setImage(R.mipmap.ic_launcher)
                val obj = JSONObject()
                try {
                    obj.put("name", "SafeGuardFamily")
                    obj.put("description", "Payment for appointment")
                    obj.put("theme.color", "")
                    obj.put("currency", "INR")
                    obj.put("amount", amount)
                    obj.put("prefill.contact", SharedPref.getUser().mobile)
                    obj.put("prefill.email", SharedPref.getUser().email)
                    checkout.open(this, obj)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        mViewModel.isBookingCompleted.observe(this) {
            if (it) {
                showToast("Your appointment is confirmed. Kindly be available on time.")
                setResult(123, Intent())
                finish()
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.d(Bundle.TAG, "Razorpay onPayment Success() called with: p0 = $p0")
        mViewModel.confirmAppointment(
            CreateAppointmentRequest(
                uid = SharedPref.getUserId()!!,
                puid = mProvider.puid,
                sel_date = apiDate,
                slot = apiTime,
                razorPayKey = p0
            )
        )
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        showToast("Payment failed, Please retry later")
        Log.d(Bundle.TAG, "Razorpay onPayment Error() called with: p0 = $p0, p1 = $p1")
        mViewModel.payFailed(
            PaymentFailRequest(
                amount = mProvider.fees,
                error = Gson().fromJson(p1, Error::class.java),
                pid = "",
                puid = mProvider.puid,
                selDate = apiDate,
                slot = apiTime,
                type = "appointment",
                uid = SharedPref.getUserId()
            )
        )
    }
}