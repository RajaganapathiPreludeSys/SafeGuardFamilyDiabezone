package com.safeguardFamily.diabezone.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.PastAppointmentAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormat
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityBookingDetailsBinding
import com.safeguardFamily.diabezone.model.response.ProfileResponse
import com.safeguardFamily.diabezone.viewModel.BookingDetailsViewModel


class BookingDetailsActivity : BaseActivity<ActivityBookingDetailsBinding, BookingDetailsViewModel>(
    R.layout.activity_booking_details,
    BookingDetailsViewModel::class.java
) {

    private lateinit var userResponse: ProfileResponse

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }
        mBinding.icHeader.tvTitle.text = "Member Details"

        if (intent.extras?.containsKey(Bundle.KEY_BOOKING_DETAILS) == true) {
            userResponse = Gson().fromJson(
                intent.extras?.getString(Bundle.KEY_BOOKING_DETAILS),
                ProfileResponse::class.java
            )
            mBinding.profile = userResponse
        }

        if (userResponse.past_appointments!!.isEmpty()) {
            mBinding.tvPlaceholder.visibility = View.VISIBLE
            mBinding.rvPastAppointments.visibility = View.GONE
        } else {
            mBinding.tvPlaceholder.visibility = View.GONE
            mBinding.rvPastAppointments.visibility = View.VISIBLE
            mBinding.rvPastAppointments.adapter =
                PastAppointmentAdapter(userResponse.past_appointments!!)
            mBinding.rvPastAppointments.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mBinding.rvPastAppointments.setHasFixedSize(true)
        }

        mBinding.rlHealthCoach.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(userResponse.health_coach))
            bundle.putString(Bundle.KEY_TITLE, "Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

        mBinding.ivRenew.setOnClickListener { navigateTo(SubscriptionActivity::class.java) }

        mBinding.llScheduleAppointment.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(userResponse.health_coach))
            bundle.putString(Bundle.KEY_TITLE, "Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

        mBinding.llScheduleCall.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(userResponse.health_coach))
            bundle.putString(Bundle.KEY_TITLE, "Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

        if (SharedPref.isMember()) {
            mBinding.llPackageDetailsContainer.visibility = View.VISIBLE
            mBinding.rlHealthCoach.visibility = View.VISIBLE
            mBinding.tvAskQues.visibility = View.VISIBLE
            mBinding.tvMemberShip.text = "Member - ${userResponse.membership!![0].pack_name}"
            mBinding.tvExpireDate.text = displayingDateFormat(userResponse.membership!![0].validity)
        } else {
            mBinding.llPackageDetailsContainer.visibility = View.GONE
            mBinding.rlHealthCoach.visibility = View.GONE
            mBinding.tvAskQues.visibility = View.GONE
            mBinding.tvMemberShip.text = "Please subscribe to become a memeber"
        }

        mBinding.tvCall.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CALL_PHONE
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE), 1
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE), 1
                    )
                }
            } else
                startActivity(
                    Intent(
                        Intent.ACTION_CALL,
                        Uri.parse("tel:+91" + userResponse.health_coach!!.mobile)
                    )
                )
        }

        mBinding.tvWhatsappCall.setOnClickListener {
            openWhatsApp(userResponse.health_coach!!.whatsapp_no)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {
                        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:9988776655")))
                    }
                }
                return
            }
        }
    }
}