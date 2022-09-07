package com.safeguardFamily.diabezone.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.*
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.DiabetesAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_URL
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.databinding.ActivityLogBookBinding
import com.safeguardFamily.diabezone.model.response.GraphItems
import com.safeguardFamily.diabezone.viewModel.LogBookViewModel
import java.util.ArrayList

class LogBookActivity : BaseActivity<ActivityLogBookBinding, LogBookViewModel>(
    R.layout.activity_log_book,
    LogBookViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icToolbar.ivRight.setImageDrawable(getDrawable(R.drawable.ic_pdf))

        mBinding.icToolbar.ivRight.setOnClickListener {
            val mBundle = Bundle()
            mBundle.putString(KEY_WEB_KEY, "PDF")
            mBundle.putString(KEY_WEB_URL, mViewModel.pdfUrl.value)
            navigateTo(WebViewActivity::class.java, mBundle)
        }

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

        mViewModel.graphData.observe(this) {
            mBinding.radioGroup1.setOnCheckedChangeListener { group, i ->
                when (i) {
                    mBinding.radioButton1.id -> loadChart(
                        it.last_7_days!!.before_meal!!,
                        "Fasting Blood Sugar"
                    )
                    mBinding.radioButton2.id -> loadChart(
                        it.last_7_days!!.after_meal!!,
                        "After Meal"
                    )
                    mBinding.radioButton3.id -> loadChart(
                        it.last_7_days!!.random!!,
                        "Random"
                    )
                }
            }
            mBinding.radioGroup1.check(mBinding.radioButton1.id)
            loadChart(it.last_7_days!!.before_meal!!, "Fasting Blood Sugar")
        }
    }

    private fun loadChart(chartData: GraphItems, type: String) {

        mBinding.tvAvg.text = chartData.summary!!.avg.toString()
        mBinding.tvTaget.text = chartData.summary!!.target.toString()
        mBinding.tvMin.text = chartData.summary!!.min.toString()
        mBinding.tvMax.text = chartData.summary!!.max.toString()

        val options = HIOptions()
//        mBinding.hc.options = options

        val xAxis = HIXAxis()
        xAxis.title = HITitle()
        xAxis.title.text = "Log Date"
        val charArray = ArrayList<String>()

        val title = HITitle()
        title.text = "Blood Sugar mg/dl"
        options.title = title

        val s1 = ArrayList<Int>()

        chartData.list!!.forEach {
            charArray.add(DateUtils.displayingDayFromAPI(it.measure_date!!)!!)
            s1.add(it.log_value!!.toInt())
        }

        xAxis.categories = charArray
        options.xAxis = object : ArrayList<HIXAxis?>() {
            init {
                add(xAxis)
            }
        }

        val yAxis = HIYAxis()
        yAxis.title = HITitle()
        yAxis.title.text = "mg/dl"
        options.yAxis = object : ArrayList<HIYAxis?>() {
            init {
                add(yAxis)
            }
        }

        val plotOptions = HIPlotOptions()
        plotOptions.line = HILine()
        plotOptions.line.dataLabels = arrayListOf<HIDataLabels>()
        plotOptions.line.enableMouseTracking = false
        options.plotOptions = plotOptions

        val series1 = HILine()
        series1.name = type
        series1.color = HIColor.initWithName("red")

        series1.data = s1

        options.series = ArrayList(listOf(series1))
        if (mBinding.hc.options == null)
            mBinding.hc.options = options
        else mBinding.hc.update(options)
    }

}