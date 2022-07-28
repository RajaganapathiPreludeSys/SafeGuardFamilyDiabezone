package com.safeguardFamily.diabezone.ui.fragment

import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.databinding.FragmentHealthVaultBinding
import com.safeguardFamily.diabezone.viewModel.HealthVaultViewModel

class HealthVaultFragment : BaseFragment<FragmentHealthVaultBinding, HealthVaultViewModel>(
    R.layout.fragment_health_vault,
    HealthVaultViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel
    }

}