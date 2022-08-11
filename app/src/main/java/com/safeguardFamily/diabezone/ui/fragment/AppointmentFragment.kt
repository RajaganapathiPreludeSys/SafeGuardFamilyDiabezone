package com.safeguardFamily.diabezone.ui.fragment

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.AppointmentAdapter
import com.safeguardFamily.diabezone.adapter.DoctorsAdapter
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.databinding.FragmentAppointmentBinding
import com.safeguardFamily.diabezone.model.DoctorModel
import com.safeguardFamily.diabezone.viewModel.AppointmentViewModel
import java.io.IOException

class AppointmentFragment : BaseFragment<FragmentAppointmentBinding, AppointmentViewModel>(
    R.layout.fragment_appointment,
    AppointmentViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
        mBinding.icHeader.tvTitle.text = getString(R.string.appointment)
        loadDoctor()
        loadAppointment()
    }

    private fun loadDoctor() {

        val json = try {
            val inputStream = requireContext().assets.open("docter.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use { it.read(buffer) }
            String(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            ""
        }

        val list: List<DoctorModel> = Gson()
            .fromJson(json, Array<DoctorModel>::class.java).toList()

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        mBinding.rvDoctors.adapter = DoctorsAdapter(list)
        mBinding.rvDoctors.layoutManager = layoutManager
        mBinding.rvDoctors.setHasFixedSize(true)
    }

    private fun loadAppointment() {
        val json = try {
            val inputStream = requireContext().assets.open("docter.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use { it.read(buffer) }
            String(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            ""
        }

        val list: List<DoctorModel> = Gson()
            .fromJson(json, Array<DoctorModel>::class.java).toList()

        val mAdapter = AppointmentAdapter(list)
        mBinding.vpAppointment.adapter = mAdapter

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