package com.safeguardFamily.diabezone.ui.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.PastAppointmentAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormat
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityBookingDetailsBinding
import com.safeguardFamily.diabezone.viewModel.BookingDetailsViewModel

class BookingDetailsActivity : BaseActivity<ActivityBookingDetailsBinding, BookingDetailsViewModel>(
    R.layout.activity_booking_details,
    BookingDetailsViewModel::class.java
) {

    override fun onceCreated() {


        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }
        mBinding.icHeader.tvTitle.text = "Member Details"

        mViewModel.userResponse.observe(this) {
            mBinding.profile = it
            if (it.past_appointments!!.isEmpty()) {
                mBinding.tvPlaceholder.visibility = View.VISIBLE
                mBinding.rvPastAppointments.visibility = View.GONE
            } else {
                mBinding.tvPlaceholder.visibility = View.GONE
                mBinding.rvPastAppointments.visibility = View.VISIBLE
                mBinding.rvPastAppointments.adapter =
                    PastAppointmentAdapter(it.past_appointments!!)
                mBinding.rvPastAppointments.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                mBinding.rvPastAppointments.setHasFixedSize(true)
            }

            if (SharedPref.isMember()) {
                mBinding.llPackageDetailsContainer.visibility = View.VISIBLE
                mBinding.rlHealthCoach.visibility = View.VISIBLE
                mBinding.tvAskQues.visibility = View.VISIBLE
                mBinding.tvMemberShip.text = "Member - ${it.membership!![0].pack_name}"
                mBinding.tvExpireDate.text = displayingDateFormat(it.membership!![0].validity)
            } else {
                mBinding.llPackageDetailsContainer.visibility = View.GONE
                mBinding.rlHealthCoach.visibility = View.GONE
                mBinding.tvAskQues.visibility = View.GONE
                mBinding.tvMemberShip.text = "Please subscribe to become a memeber"
            }
        }

        mBinding.rlHealthCoach.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(
                Bundle.KEY_DOCTOR,
                Gson().toJson(mViewModel.userResponse.value!!.health_coach)
            )
            bundle.putString(Bundle.KEY_TITLE, "Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

        mBinding.ivRenew.setOnClickListener { navigateTo(SubscriptionActivity::class.java) }

        mBinding.llScheduleAppointment.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(
                Bundle.KEY_DOCTOR,
                Gson().toJson(mViewModel.userResponse.value!!.health_coach)
            )
            bundle.putString(Bundle.KEY_TITLE, "Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

        mBinding.llScheduleCall.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(
                Bundle.KEY_DOCTOR,
                Gson().toJson(mViewModel.userResponse.value!!.health_coach)
            )
            bundle.putString(Bundle.KEY_TITLE, "Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

        val requestSinglePermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) startActivity(
                Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:+91" + mViewModel.userResponse.value!!.health_coach!!.mobile)
                )
            )
            else showToast("Permission Denied by user for making calls")
        }

        mBinding.tvCall.setOnClickListener {
            requestSinglePermission.launch(Manifest.permission.CALL_PHONE)
        }

        mBinding.tvWhatsappCall.setOnClickListener {
            openWhatsApp(mViewModel.userResponse.value!!.health_coach!!.whatsapp_no)
        }
    }
}