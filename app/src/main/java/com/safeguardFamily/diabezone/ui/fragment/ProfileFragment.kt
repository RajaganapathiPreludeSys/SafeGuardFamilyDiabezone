package com.safeguardFamily.diabezone.ui.fragment

import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.databinding.FragmentProfileBinding
import com.safeguardFamily.diabezone.ui.graph.draw.data.InputData
import com.safeguardFamily.diabezone.viewModel.ProfileViewModel
import java.util.concurrent.TimeUnit

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(
    R.layout.fragment_profile,
    ProfileViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
        mBinding.charView.setData(createChartData())
    }

    private fun createChartData(): List<InputData> {
        val dataList = ArrayList<InputData>()
        dataList.add(InputData(110))
        dataList.add(InputData(25))
        dataList.add(InputData(20))
        dataList.add(InputData(0))
        dataList.add(InputData(20))
        dataList.add(InputData(50))
        dataList.add(InputData(40))

        var currMillis = System.currentTimeMillis()
        currMillis -= currMillis % TimeUnit.DAYS.toMillis(1)
        for (i in dataList.indices) {
            val position = (dataList.size - 1 - i).toLong()
            val offsetMillis = TimeUnit.DAYS.toMillis(position)
            val millis = currMillis - offsetMillis
            dataList[i].millis = millis
        }
        return dataList
    }

}