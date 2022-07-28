package com.safeguardFamily.diabezone.ui.fragment

import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.databinding.FragmentAppointmentBinding
import com.safeguardFamily.diabezone.viewModel.AppointmentViewModel

class AppointmentFragment : BaseFragment<FragmentAppointmentBinding, AppointmentViewModel>(
    R.layout.fragment_appointment,
    AppointmentViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
    }

}