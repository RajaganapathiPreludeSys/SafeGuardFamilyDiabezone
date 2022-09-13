package com.safeguardFamily.diabezone.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.DiabetesAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_URL
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.databinding.ActivityLogBookBinding
import com.safeguardFamily.diabezone.model.response.GraphItems
import com.safeguardFamily.diabezone.viewModel.LogBookViewModel

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
        mBinding.icToolbar.tvTitle.text = "Daily Diabetes Log"
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
                    mBinding.radioButton1.id -> bindChart(
                        it.last_7_days!!.before_meal!!,
                        "Fasting Blood Sugar"
                    )
                    mBinding.radioButton2.id -> bindChart(
                        it.last_7_days!!.after_meal!!,
                        "After Meal"
                    )
                    mBinding.radioButton3.id -> bindChart(
                        it.last_7_days!!.random!!,
                        "Random"
                    )
                }
            }
            mBinding.radioGroup1.check(mBinding.radioButton1.id)
            bindChart(it.last_7_days!!.before_meal!!, "Fasting Blood Sugar")
        }
    }

    private fun bindChart(chartData: GraphItems, s: String) {
        mBinding.tvAvg.text = chartData.summary!!.avg.toString()
        mBinding.tvTaget.text = chartData.summary!!.target.toString()
        mBinding.tvMin.text = chartData.summary!!.min.toString()
        mBinding.tvMax.text = chartData.summary!!.max.toString()

        val months = ArrayList<String>()
        chartData.list!!.reversed().forEach {
            months.add(DateUtils.displayingDayFromAPI(it.measure_date!!)!!)
        }

        if (chartData.list!!.isNotEmpty()) {

            mBinding.chart1.visibility = View.VISIBLE
            mBinding.tvChartPlaceholder.visibility = View.GONE

            mBinding.chart1.description.isEnabled = false
            mBinding.chart1.setBackgroundColor(Color.WHITE)
            mBinding.chart1.setDrawGridBackground(false)
            mBinding.chart1.setDrawBarShadow(false)
            mBinding.chart1.isHighlightFullBarEnabled = false
            mBinding.chart1.setTouchEnabled(false)
            mBinding.chart1.setPinchZoom(false)
            mBinding.chart1.isDoubleTapToZoomEnabled = false

            mBinding.chart1.drawOrder = arrayOf(DrawOrder.LINE)
            val l = mBinding.chart1.legend
            l.isWordWrapEnabled = true
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)

            val rightAxis = mBinding.chart1.axisRight
            rightAxis.textSize = 12f
            rightAxis.setDrawGridLines(false)

            val leftAxis = mBinding.chart1.axisLeft
            leftAxis.textSize = 12f
            leftAxis.setDrawGridLines(false)

            val xAxis = mBinding.chart1.xAxis
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = 50f
            xAxis.axisMinimum = 0f
            xAxis.granularity = 1f
            xAxis.textSize = 12f

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return months[value.toInt() % months.size]
                }
            }
            val data = CombinedData()

            data.setData(generateLineData(chartData, s))

            xAxis.axisMaximum = data.xMax + 0.25f

            mBinding.chart1.animateY(2000, Easing.EaseOutBack);

            mBinding.chart1.data = data
            mBinding.chart1.invalidate()
        } else {
            mBinding.chart1.visibility = View.GONE
            mBinding.tvChartPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun generateLineData(chartData: GraphItems, s: String): LineData {
        val d = LineData()
        val entries = ArrayList<Entry>()

        chartData.list!!.reversed().forEachIndexed { i, l ->
            entries.add(Entry(i + 0.0f, l.log_value!! + 0f))
        }
        val set = LineDataSet(entries, s)
        set.color = getColor(R.color.blueOpacity)
        set.lineWidth = 2.5f
        set.setCircleColor(getColor(R.color.blue))
        set.circleRadius = 3f
        set.fillColor = getColor(R.color.blue)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawValues(true)
        set.valueTextSize = 12f
        set.valueTextColor = getColor(R.color.black)
        set.axisDependency = YAxis.AxisDependency.RIGHT
        d.addDataSet(set)
        return d
    }

}