package com.safeguardFamily.diabezone.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.DaysAdapter
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
        }

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.btChat.setOnClickListener { openWhatsApp(provider.mobile) }

        mBinding.btCall.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(provider))
            navigateTo(ScheduleAppointmentActivity::class.java, bundle, true)
        }

        mBinding.rvAvailability.adapter = DaysAdapter(provider.timings.days.split(" "))
        mBinding.rvAvailability.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvAvailability.setHasFixedSize(true)
    }

}