package com.safeguardFamily.diabezone.ui.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.ui.adapter.AppointmentAdapter
import com.safeguardFamily.diabezone.ui.adapter.DoctorsAdapter
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.databinding.FragmentAppointmentBinding
import com.safeguardFamily.diabezone.viewModel.AppointmentViewModel

class AppointmentFragment : BaseFragment<FragmentAppointmentBinding, AppointmentViewModel>(
    R.layout.fragment_appointment,
    AppointmentViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
        loadDoctor()
        loadAppointment()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getAppointmentData()
    }

    private fun loadDoctor() {
        mViewModel.providers.observe(this) {
            mBinding.rvDoctors.adapter = DoctorsAdapter(it)
            mBinding.rvDoctors.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            mBinding.rvDoctors.setHasFixedSize(true)
        }
    }

    private fun loadAppointment() {
        mViewModel.upcomingAppointment.observe(this) {
            if (it.isNotEmpty()) {
                mBinding.tvUpcomingAppointment.visibility = View.VISIBLE
                mBinding.tvBookAnother.visibility = View.VISIBLE
                mBinding.cvContainer.visibility = View.VISIBLE
                mBinding.clBanner.visibility = View.GONE
            } else {
                mBinding.tvUpcomingAppointment.visibility = View.GONE
                mBinding.tvBookAnother.visibility = View.GONE
                mBinding.cvContainer.visibility = View.GONE
                mBinding.clBanner.visibility = View.VISIBLE
            }

            mBinding.llAppointmentIndicator.removeAllViews()
            val mAdapter = AppointmentAdapter(it)
            mBinding.vpAppointment.adapter = mAdapter

            if (it.size > 1) {
                val indicators = arrayOfNulls<ImageView>(mAdapter.itemCount)
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(8, 0, 8, 0)
                for (i in indicators.indices) {
                    indicators[i] = ImageView(context)
                    indicators[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.onboarding_indicator_inactive
                        )
                    )
                    indicators[i]!!.layoutParams = layoutParams
                    mBinding.llAppointmentIndicator.addView(indicators[i])
                }

                setCurrentIndicators(0)

                mBinding.vpAppointment.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentIndicators(position)
                    }
                })
            }
        }
    }

    private fun setCurrentIndicators(index: Int) {
        val childCount: Int = mBinding.llAppointmentIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = mBinding.llAppointmentIndicator.getChildAt(i) as ImageView
            if (i == index) imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.onboarding_indicator_active
                )
            ) else imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.onboarding_indicator_inactive
                )
            )
        }
    }
}