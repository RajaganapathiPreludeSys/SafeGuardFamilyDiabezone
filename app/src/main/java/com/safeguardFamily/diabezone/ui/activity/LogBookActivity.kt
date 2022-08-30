package com.safeguardFamily.diabezone.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.DiabetesAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.databinding.ActivityLogBookBinding
import com.safeguardFamily.diabezone.viewModel.LogBookViewModel

class LogBookActivity : BaseActivity<ActivityLogBookBinding, LogBookViewModel>(
    R.layout.activity_log_book,
    LogBookViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icToolbar.ivBack.setOnClickListener { finish() }
        mBinding.icToolbar.tvTitle.text = "Daily Diabetes Logbook"
        loadData()
    }

    private fun loadData() {
        mViewModel.logs.observe(this) {
            mBinding.rvDiabetes.adapter = DiabetesAdapter(it) {
                mViewModel.editDiabetesLog(it)
            }
            mBinding.rvDiabetes.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mBinding.rvDiabetes.setHasFixedSize(true)
        }
    }

}