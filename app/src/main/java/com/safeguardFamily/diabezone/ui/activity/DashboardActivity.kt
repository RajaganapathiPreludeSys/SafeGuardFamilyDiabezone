package com.safeguardFamily.diabezone.ui.activity

import androidx.fragment.app.Fragment
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.databinding.ActivityDashboardBinding
import com.safeguardFamily.diabezone.ui.fragment.AppointmentFragment
import com.safeguardFamily.diabezone.ui.fragment.HealthVaultFragment
import com.safeguardFamily.diabezone.ui.fragment.HomeFragment
import com.safeguardFamily.diabezone.ui.fragment.ProfileFragment
import com.safeguardFamily.diabezone.viewModel.DashboardViewModel

class DashboardActivity : BaseActivity<ActivityDashboardBinding, DashboardViewModel>(
    R.layout.activity_dashboard,
    DashboardViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        val home = HomeFragment()
        val healthVault = HealthVaultFragment()
        val appointment = AppointmentFragment()
        val profile = ProfileFragment()

        setCurrentFragment(home)

        mBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(home)
                R.id.healthVault -> setCurrentFragment(healthVault)
                R.id.appointment -> setCurrentFragment(appointment)
                R.id.profile -> setCurrentFragment(profile)
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}