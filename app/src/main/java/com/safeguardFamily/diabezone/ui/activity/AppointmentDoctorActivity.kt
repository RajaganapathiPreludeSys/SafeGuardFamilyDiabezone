package com.safeguardFamily.diabezone.ui.activity

import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.databinding.ActivityAppointmentDoctorBinding
import com.safeguardFamily.diabezone.viewModel.AppointmentDoctorViewModel

class AppointmentDoctorActivity :
    BaseActivity<ActivityAppointmentDoctorBinding, AppointmentDoctorViewModel>(
        R.layout.activity_appointment_doctor,
        AppointmentDoctorViewModel::class.java
    ) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.llMakeAppointment.setOnClickListener { navigateTo(ScheduleAppointmentActivity::class.java) }
        mBinding.ivBack.setOnClickListener { finish() }

    }

}