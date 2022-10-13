package com.safeguardFamily.diabezone.ui.activity

import android.graphics.Color
import android.graphics.Typeface
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_URL
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.databinding.ActivityLogBookBinding
import com.safeguardFamily.diabezone.model.response.GraphItems
import com.safeguardFamily.diabezone.ui.adapter.DiabetesAdapter
import com.safeguardFamily.diabezone.viewModel.LogBookViewModel
import kotlin.math.roundToInt

class LogBookActivity : BaseActivity<ActivityLogBookBinding, LogBookViewModel>(
    R.layout.activity_log_book,
    LogBookViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icToolbar.ivRight.setImageDrawable(getDrawable(R.drawable.ic_pdf))

        mBinding.icToolbar.ivRight.setOnClickListener {

            if (mViewModel.pdfUrl.value!!.length > 2)  {
                val mBundle = Bundle()
                mBundle.putString(KEY_WEB_KEY, "PDF")
                mBundle.putString(KEY_WEB_URL, mViewModel.pdfUrl.value)
                navigateTo(PDFActivity::class.java, mBundle)
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Open log book PDF")
                }
            } else showToast("No log values available for now.")

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
                        it!!.lifetime!!.before_meal!!,
                        "Fasting"
                    )
                    mBinding.radioButton2.id -> bindChart(
                        it!!.lifetime!!.after_meal!!,
                        "After Meal"
                    )
                    mBinding.radioButton3.id -> bindChart(
                        it!!.lifetime!!.random!!,
                        "Random"
                    )
                }
            }
            mBinding.radioGroup1.check(mBinding.radioButton1.id)
            bindChart(it!!.lifetime!!.before_meal!!, "Fasting")
        }
    }

    private fun bindChart(chartData: GraphItems, s: String) {
        mBinding.tvAvg.text = chartData.summary!!.avg.toString()
        mBinding.tvTaget.text =
            "${chartData.summary!!.minTarget}-${chartData.summary!!.maxTarget}"
        mBinding.tvMin.text = chartData.summary!!.min.toString()
        mBinding.tvMax.text = chartData.summary!!.max.toString()
        mBinding.tvGraphTitle.text = "Blood Sugar - $s"

        val months = ArrayList<String>()
        chartData.list!!.reversed().forEach {
            months.add(DateUtils.displayingDayFromAPI(it.measure_date!!)!!)
        }

        if (chartData.list!!.isNotEmpty()) {

            mBinding.rlGraphContainer.visibility = View.VISIBLE
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
            xAxis.labelRotationAngle = -50f
            xAxis.axisMinimum = 0f
            xAxis.labelCount = chartData.list!!.size
            xAxis.granularity = 1f
            xAxis.textSize = 12f

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return months[value.toInt() % months.size]
                }
            }

            val data = CombinedData()

            data.setData(generateLineData(chartData))

            if (chartData.list!!.size > 1) xAxis.axisMinimum = -0.3f

            mBinding.chart1.animateY(2000, Easing.EaseOutBack)

            mBinding.chart1.data = data
            mBinding.chart1.axisRight.isEnabled = false
            mBinding.chart1.legend.isEnabled = false
            mBinding.chart1.axisLeft.setDrawGridLines(false)
            mBinding.chart1.xAxis.setDrawGridLines(false)
            mBinding.chart1.invalidate()
            mBinding.tvResetZoom.setOnClickListener {
                mBinding.chart1.fitScreen()
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Reset Zoom in Log book")
                }
            }
        } else {
            mBinding.rlGraphContainer.visibility = View.GONE
            mBinding.tvChartPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun generateLineData(chartData: GraphItems): LineData {
        val d = LineData()
        val entries = ArrayList<Entry>()

        val colors = java.util.ArrayList<Int>()
        chartData.list!!.reversed().forEachIndexed { i, l ->
            entries.add(Entry(i + 0.0f, l.log_value!! + 0f))
            if (l.status == "hyper" || l.status == "hypo") {
                colors.add(getColor(R.color.red))
            } else colors.add(getColor(R.color.blue))
        }
        val set = LineDataSet(entries, "")
        set.color = getColor(R.color.blueOpacity)
        set.lineWidth = 4f
        set.setCircleColor(getColor(R.color.blue))
        set.circleRadius = 5f
        set.fillColor = getColor(R.color.blue)
        set.mode = LineDataSet.Mode.LINEAR
        set.setDrawValues(true)
        set.valueTextSize = 13f
        set.valueTypeface = Typeface.defaultFromStyle(Typeface.BOLD)
        set.axisDependency = YAxis.AxisDependency.RIGHT
        set.circleColors = colors
        set.setValueTextColors(colors)
        d.addDataSet(set)
        d.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.roundToInt().toString()
            }
        })
        return d
    }
}