package com.safeguardFamily.diabezone.ui.activity

import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.databinding.ActivityAppointmentDoctorBinding
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.viewModel.AppointmentDoctorViewModel

class DoctorDetailsActivity :
    BaseActivity<ActivityAppointmentDoctorBinding, AppointmentDoctorViewModel>(
        R.layout.activity_appointment_doctor,
        AppointmentDoctorViewModel::class.java
    ) {

    lateinit var provider: Provider
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        if (intent.extras?.containsKey(Bundle.KEY_DOCTOR) == true) {
            provider = Gson()
                .fromJson(intent.extras?.getString(Bundle.KEY_DOCTOR), Provider::class.java)
            mBinding.provider = provider
        }

        mBinding.llMakeAppointment.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(provider))
            navigateTo(ScheduleAppointmentActivity::class.java, bundle)
        }
        mBinding.ivBack.setOnClickListener { finish() }

    }


}
