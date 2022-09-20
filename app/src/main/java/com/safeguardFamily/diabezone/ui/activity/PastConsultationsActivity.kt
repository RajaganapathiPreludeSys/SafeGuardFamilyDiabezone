package com.safeguardFamily.diabezone.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.PastAppointmentAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.databinding.ActivityPastConsultationBinding
import com.safeguardFamily.diabezone.model.response.PastAppointment
import com.safeguardFamily.diabezone.viewModel.PastConsultationViewModel

class PastConsultationsActivity :
    BaseActivity<ActivityPastConsultationBinding, PastConsultationViewModel>(
        R.layout.activity_past_consultation,
        PastConsultationViewModel::class.java
    ) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }
        mBinding.icHeader.tvTitle.text = "Past Consultations"

        if (intent.extras?.containsKey(Bundle.KEY_PAST_CONSULTS) == true) {

            val list: List<PastAppointment> = Gson().fromJson(
                intent.extras?.getString(Bundle.KEY_PAST_CONSULTS),
                Array<PastAppointment>::class.java
            ).toList()

            if (list.isEmpty()) {
                mBinding.tvPlaceholder.visibility = View.VISIBLE
                mBinding.rvPastAppointments.visibility = View.GONE
            } else {
                mBinding.tvPlaceholder.visibility = View.GONE
                mBinding.rvPastAppointments.visibility = View.VISIBLE
                mBinding.rvPastAppointments.adapter =
                    PastAppointmentAdapter(list)
                mBinding.rvPastAppointments.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                mBinding.rvPastAppointments.setHasFixedSize(true)
            }

        }


    }

}