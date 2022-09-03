package com.safeguardFamily.diabezone.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.*
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.DoctorsAdapter
import com.safeguardFamily.diabezone.adapter.NotificationAdapter
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormat
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateTimeFormatToAPIFormat
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.appointment
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.diabetesLog
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.healthVault
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.programs
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.providers
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.DialogDateTimeBinding
import com.safeguardFamily.diabezone.databinding.FragmentHomeBinding
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest
import com.safeguardFamily.diabezone.ui.activity.DashboardActivity
import com.safeguardFamily.diabezone.ui.activity.LogBookActivity
import com.safeguardFamily.diabezone.viewModel.HomeViewModel
import java.util.*

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        R.layout.fragment_home,
        HomeViewModel::class.java
    ) {

    private var formattedDate = ""

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        loadNotification()

        val items = arrayOf(" Select meal type", "Before Meal", "After Meal", "Random")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        mBinding.spType.adapter = adapter

        mBinding.btAddToLog.setOnClickListener {
            when {
                mBinding.tvTimeValue.text == "Select Time" -> showToast("Select a valid date and time")
                mBinding.etBloodSugar.text?.isNotEmpty() == false -> showToast("Enter a valid blood sugar value")
                mBinding.spType.selectedItem == " Select meal type" -> showToast("Select a valid meal type")
                mBinding.etBloodSugar.text.toString()
                    .toInt() <= 0 -> showToast("Enter a valid blood sugar value")
                else -> {
                    val request = DiabetesLogRequest(
                        measureDate = formattedDate,
                        logValue = mBinding.etBloodSugar.text.toString().toInt(),
                        uid = SharedPref.getUserId()!!,
                        period = when (mBinding.spType.selectedItem) {
                            "Before Meal" -> "before_meal"
                            "After Meal" -> "after_meal"
                            "Random" -> "random"
                            else -> ""
                        }
                    )
                    mViewModel.addDiabetesLog(request) {
                        mBinding.tvTimeValue.text = getString(R.string.select_time)
                        mBinding.etBloodSugar.text = null
                        mBinding.spType.adapter = adapter
                        showToast("Diabetes Log Saved")
                    }
                }
            }
        }

        mBinding.tlTimeContainer.setOnClickListener { showDateTimeDialog() }

        mBinding.ivOpenLogs.setOnClickListener { navigateTo(LogBookActivity::class.java) }

        val date = arrayOf("Last week's data", "Last month's data", "All data")
        mBinding.spDate.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, date)

        mBinding.spDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d(TAG, "onItemSelected() called with:  p2 = $p2, p3 = $p3")
                val chartCount = when (p2) {
                    0 -> 7
                    1 -> 30
                    2 -> 50
                    else -> 7
                }
                Log.d(TAG, "onItemSelected() called with:  chartCount = $chartCount")
                loadChart(chartCount)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        mBinding.ivOne.setOnClickListener {
            (activity as DashboardActivity?)!!.setCurrentFragment(
                (activity as DashboardActivity?)!!.appointment
            )
        }
        mBinding.ivTwo.setOnClickListener {
            (activity as DashboardActivity?)!!.setCurrentFragment(
                (activity as DashboardActivity?)!!.healthVault
            )
        }
        mBinding.ivThree.setOnClickListener {
            (activity as DashboardActivity?)!!.setCurrentFragment(
                (activity as DashboardActivity?)!!.healthVault
            )
        }

    }

    private fun loadNotification() {
        mViewModel.notifications.observe(this) {
            val mAdapter = NotificationAdapter(it) { string ->
                when (string) {
                    appointment -> (activity as DashboardActivity?)!!.setCurrentFragment(
                        (activity as DashboardActivity?)!!.appointment
                    )
                    providers -> (activity as DashboardActivity?)!!.setCurrentFragment(
                        (activity as DashboardActivity?)!!.appointment
                    )
                    diabetesLog -> navigateTo(LogBookActivity::class.java)
                    healthVault -> (activity as DashboardActivity?)!!.setCurrentFragment(
                        (activity as DashboardActivity?)!!.healthVault
                    )
                    programs -> {

                    }
                }
            }
            mBinding.vpNotification.adapter = mAdapter

            if (it.size > 1) {
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
                    mBinding.llNotificationIndicator.addView(indicators[i])
                }

                setCurrentIndicators(0)

                mBinding.vpNotification.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentIndicators(position)
                    }
                })
            }
        }

    }

    private fun setCurrentIndicators(index: Int) {
        val childCount: Int = mBinding.llNotificationIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = mBinding.llNotificationIndicator.getChildAt(i) as ImageView
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

    private fun showDateTimeDialog() {
        var dateString = ""
        var timeString = ""
        val timeValidator = Calendar.getInstance()
        val dialogBinding: DialogDateTimeBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.dialog_date_time,
                null,
                false
            )

        val mDialog = AlertDialog.Builder(requireContext(), 0).create()

        mDialog.apply {
            setView(dialogBinding.root)
            setCancelable(false)
        }.show()

        val mCalendar = Calendar.getInstance()
        dialogBinding.datePicker.maxDate = mCalendar.timeInMillis
        dialogBinding.datePicker.init(
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            val m = month + 1
            dateString = "$year-$m-$day"
            dialogBinding.tvSelectedVal.text = "${displayingDateFormat(dateString)}$timeString"
            timeValidator.set(year, m, day)
        }

        dialogBinding.timePicker.setOnTimeChangedListener { _, _hour, minute ->
            var hour = _hour
            val amPm: String
            when {
                hour == 0 -> {
                    hour += 12
                    amPm = "AM"
                }
                hour == 12 -> amPm = "PM"
                hour > 12 -> {
                    hour -= 12
                    amPm = "PM"
                }
                else -> amPm = "AM"
            }
            val hourString = if (hour < 10) "0$hour" else hour
            val min = if (minute < 10) "0$minute" else minute
            timeString = " $hourString:$min $amPm"
            dialogBinding.tvSelectedVal.text =
                if (dateString.length > 1) "${displayingDateFormat(dateString)}$timeString"
                else timeString

        }

        dialogBinding.btCancel.setOnClickListener { mDialog.dismiss() }
        dialogBinding.btPickDate.setOnClickListener {
            if (dateString.isEmpty()) showToast("Select a valid date")
            else if (timeString.isEmpty()) showToast("Select a valid time")
            else {
                mBinding.tvTimeValue.text =
                    if (dateString.length > 1) "${displayingDateFormat(dateString)}$timeString"
                    else timeString
                formattedDate =
                    displayingDateTimeFormatToAPIFormat("${displayingDateFormat(dateString)}$timeString")!!
                Log.d(TAG, "showDateTimeDialog formattedDate: $formattedDate")
                mDialog.dismiss()
            }
        }
    }

    private fun loadChart(chartCount: Int) {
        val options = HIOptions()
        mBinding.hc.options = options
        val chart = HIChart()
        chart.zoomType = "x"
        options.chart = chart

        val title = HITitle()
        title.text = "Blood Sugar mg/dl"
        options.title = title

        val xAxis = HIXAxis()
        xAxis.title = HITitle()
        xAxis.title.text = "Date"
        val charArray = ArrayList<String>()
        for (i in chartCount..1) charArray.add(i.toString())
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
        series1.name = "Before meal"
        series1.color = HIColor.initWithName("red")
        val s1 = ArrayList<Int>()
        for (i in 1..chartCount) s1.add((110..160).random())
        val series1Data =
            arrayOf<Number>(
                7.0,
                6.9,
                9.5,
                14.5,
                18.4,
                21.5,
                25.2,
                26.5,
                23.3,
                18.3,
                13.9,
                9.6,
                7.0,
                6.9,
                9.5,
                14.5,
                18.4,
                21.5,
                25.2,
                26.5,
                23.3,
                18.3,
                13.9,
                9.6,
                40
            )
        series1.data = s1
//        series1.data = ArrayList(listOf(*series1Data))

        val series2 = HILine()
        series2.name = "After meal"
        series2.color = HIColor.initWithName("blue")
        val s2 = ArrayList<Int>()
        for (i in 1..chartCount) s2.add((100..150).random())
        val series2Data =
            arrayOf<Number?>(
                3.9,
                4.2,
                0,
                0,
                0,
                5.7,
                15.2,
                11.9,
                15.2,
                null,
                null,
                null,
                0,
                0,
                0,
                0,
                0,
                null,
                15.2,
                null,
                null,
                null,
                15.2,
                null,
                17.0,
                16.6,
                14.2,
                10.3,
                6.6,
                4.8,
                40
            )
//        series2.data = s2
        series2.data = ArrayList(listOf(*series2Data))

        val series3 = HILine()
        series3.name = "Random"
        series3.color = HIColor.initWithName("green")
        val s3 = ArrayList<Int>()
        for (i in 1..chartCount) s3.add((120..170).random())
        val series3Data =
            arrayOf<Number?>(
                3.9,
                4.2,
                0,
                0,
                0,
                5.7,
                15.2,
                11.9,
                15.2,
                null,
                null,
                null,
                0,
                0,
                0,
                0,
                0,
                null,
                15.2,
                null,
                null,
                null,
                15.2,
                null,
                17.0,
                16.6,
                14.2,
                10.3,
                6.6,
                4.8,
                25
            )
        series3.data = s3

        options.series = ArrayList(listOf(series1, series2, series3))
        mBinding.hc.options = options
    }

}
