package com.safeguardFamily.diabezone.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.DiabetesAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.databinding.ActivityLogBookBinding
import com.safeguardFamily.diabezone.model.DiabetesModel
import com.safeguardFamily.diabezone.viewModel.LogBookViewModel

class LogBookActivity : BaseActivity<ActivityLogBookBinding, LogBookViewModel>(
    R.layout.activity_log_book,
    LogBookViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icToolbar.ivBack.setonclickListener{ finish()}
        mBinding.icToolbar.tvTitle.text = "Daily Diabetes Logbook"
        loadData()
    }

    private fun loadData() {
        val item1 = DiabetesModel()
        item1.time = "11 July 2022, 5:15 PM"
        item1.bloodSugar = "180 mg/dL"
        item1.type = "After Meal"

        val item2 = DiabetesModel()
        item2.time = "11 July 2022, 5:15 PM"
        item2.bloodSugar = "160 mg/dL"
        item2.type = "Before Meal"

        val item3 = DiabetesModel()
        item3.time = "11 July 2022, 5:15 PM"
        item3.bloodSugar = "140 mg/dL"
        item3.type = "Random"

        val item4 = DiabetesModel()
        item4.time = "11 July 2022, 5:15 PM"
        item4.bloodSugar = "120 mg/dL"
        item4.type = "After Meal"

        val item5 = DiabetesModel()
        item5.time = "11 July 2022, 5:15 PM"
        item5.bloodSugar = "110 mg/dL"
        item5.type = "Before Meal"

        val item6 = DiabetesModel()
        item6.time = "11 July 2022, 5:15 PM"
        item6.bloodSugar = "130 mg/dL"
        item6.type = "Random"

        val item7 = DiabetesModel()
        item7.time = "11 July 2022, 5:15 PM"
        item7.bloodSugar = "150 mg/dL"
        item7.type = "After Meal"

        val item8 = DiabetesModel()
        item8.time = "11 July 2022, 5:15 PM"
        item8.bloodSugar = "170 mg/dL"
        item8.type = "Before Meal"

        val item9 = DiabetesModel()
        item9.time = "11 July 2022, 5:15 PM"
        item9.bloodSugar = "190 mg/dL"
        item9.type = "Random"

        val items = ArrayList<DiabetesModel>()
        items.add(item1)
        items.add(item2)
        items.add(item3)
        items.add(item4)
        items.add(item5)
        items.add(item6)
        items.add(item7)
        items.add(item8)
        items.add(item9)

        mBinding.rvDiabetes.adapter = DiabetesAdapter(items)
        mBinding.rvDiabetes.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvDiabetes.setHasFixedSize(true)
    }

}