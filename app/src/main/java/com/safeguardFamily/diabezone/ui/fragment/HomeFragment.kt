package com.safeguardFamily.diabezone.ui.fragment

import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.databinding.FragmentHomeBinding
import com.safeguardFamily.diabezone.viewModel.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    R.layout.fragment_home,
    HomeViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
    }

}