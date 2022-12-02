package com.safeguardFamily.diabezone.ui.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
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
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
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
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_URL
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.Bundle.date12Format
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormat
import com.safeguardFamily.diabezone.common.DateUtils.displayingDayFromAPI
import com.safeguardFamily.diabezone.common.DateUtils.formatTo24Hrs
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.appointment
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.diabetesLog
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.healthVault
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.programs
import com.safeguardFamily.diabezone.common.NotificationNavigationFlat.providers
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.DialogDateBinding
import com.safeguardFamily.diabezone.databinding.DialogTimeBinding
import com.safeguardFamily.diabezone.databinding.FragmentHomeBinding
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest
import com.safeguardFamily.diabezone.model.response.GraphItems
import com.safeguardFamily.diabezone.ui.activity.DashboardActivity
import com.safeguardFamily.diabezone.ui.activity.LogBookActivity
import com.safeguardFamily.diabezone.ui.activity.PDFActivity
import com.safeguardFamily.diabezone.ui.activity.SubscriptionActivity
import com.safeguardFamily.diabezone.ui.adapter.NotificationAdapter
import com.safeguardFamily.diabezone.viewModel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        R.layout.fragment_home,
        HomeViewModel::class.java
    ) {

    private var mProfile = SharedPref.getUser()
    private var dateString = ""
    private var timeString = ""
    private var pdfUrl = ""

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        loadNotification()

        val items = arrayOf("Select type", "Fasting", "After Meal", "Random")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, items)
        mBinding.spType.adapter = adapter

        var mCalendar = Calendar.getInstance()
        dateString = "${mCalendar.get(Calendar.YEAR)}-${mCalendar.get(Calendar.MONTH) + 1}-${
            mCalendar.get(Calendar.DAY_OF_MONTH)
        }"
        mBinding.tvDate.text = "${displayingDateFormat(dateString)}"
        timeString = SimpleDateFormat(date12Format, Locale.getDefault()).format(Date())
        mBinding.tvTime.text = timeString

        mBinding.spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mBinding.rlTypeContainer.setBackgroundResource(R.drawable.bg_blue_border)
            }
        }

        mBinding.etBloodSugar.addTextChangedListener {
            mBinding.tlBloodSugarContainer.setBackgroundResource(R.drawable.bg_blue_border)
        }

        mBinding.btAddLog.setOnClickListener {
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Add diabetes logs")
            }
            when {
                mBinding.etBloodSugar.text?.isNotEmpty() == false -> {
                    mBinding.tlBloodSugarContainer.setBackgroundResource(R.drawable.bg_red_border)
                    showToast("Enter a valid blood sugar value")
                }
                mBinding.spType.selectedItem == "Select type" -> {
                    mBinding.rlTypeContainer.setBackgroundResource(R.drawable.bg_red_border)
                    showToast("Select a valid meal type")
                }
                mBinding.etBloodSugar.text.toString()
                    .toInt() <= 0 -> showToast("Enter a valid blood sugar value")
                else -> {
                    Log.d(TAG, "onceCreated: $dateString $timeString")
                    Log.d(TAG, "onceCreated: $dateString ${formatTo24Hrs(timeString)}")
                    val request = DiabetesLogRequest(
                        measureDate = "$dateString ${formatTo24Hrs(timeString)}",
                        logValue = mBinding.etBloodSugar.text.toString().toInt(),
                        uid = SharedPref.getUserId()!!,
                        period = when (mBinding.spType.selectedItem) {
                            "Fasting" -> "before_meal"
                            "After Meal" -> "after_meal"
                            "Random" -> "random"
                            else -> "before_meal"
                        }
                    )
                    if (mBinding.etBloodSugar.text.toString().toInt() <= 60) {
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder
                            .setMessage("${mProfile.name} your blood sugar value is very low.")
                            .setCancelable(true)
                            .setPositiveButton("ok") { dialog, id ->
                                mViewModel.addDiabetesLog(request) {
                                    loadHomeGraph()
                                    mCalendar = Calendar.getInstance()
                                    dateString =
                                        "${mCalendar.get(Calendar.YEAR)}-${mCalendar.get(Calendar.MONTH) + 1}-${
                                            mCalendar.get(Calendar.DAY_OF_MONTH)
                                        }"
                                    mBinding.tvDate.text = "${displayingDateFormat(dateString)}"
                                    mBinding.tvTime.text =
                                        SimpleDateFormat(date12Format, Locale.getDefault()).format(
                                            Date()
                                        )
                                    mBinding.etBloodSugar.text = null
                                    mBinding.spType.adapter = adapter
                                    showToast("Diabetes Log Saved")
                                }
                            }
                        val alert = dialogBuilder.create()
                        alert.setTitle("Alert - Blood Sugar very low")
                        alert.show()
                    } else mViewModel.addDiabetesLog(request) {
                        loadHomeGraph()
                        mCalendar = Calendar.getInstance()
                        dateString =
                            "${mCalendar.get(Calendar.YEAR)}-${mCalendar.get(Calendar.MONTH) + 1}-${
                                mCalendar.get(Calendar.DAY_OF_MONTH)
                            }"
                        mBinding.tvDate.text = "${displayingDateFormat(dateString)}"
                        mBinding.tvTime.text =
                            SimpleDateFormat(date12Format, Locale.getDefault()).format(
                                Date()
                            )
                        mBinding.etBloodSugar.text = null
                        mBinding.spType.adapter = adapter
                        showToast("Diabetes Log Saved")
                    }
                }
            }
        }

        mBinding.ivRedBanner.setOnClickListener {
            navigateTo(SubscriptionActivity::class.java)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Subscription Page")
            }
        }
        mBinding.rlContainer.setOnClickListener {
            (activity as DashboardActivity?)!!.setCurrentFragment(
                (activity as DashboardActivity?)!!.profile
            )
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Profile Page from home")
            }
        }
        mBinding.ivProgram.setOnClickListener {
            navigateTo(SubscriptionActivity::class.java)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Subscription Page")
            }
        }
        mBinding.tlDateContainer.setOnClickListener {
            showDateDialog()
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Open date picker")
            }
        }
        mBinding.tlTimeContainer.setOnClickListener {
            showTimeDialog()
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Open Time picker")
            }
        }
        mBinding.ivOpenLogs.setOnClickListener {
            navigateTo(LogBookActivity::class.java)
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Log book screen from home")
            }
        }

        mBinding.cvOne.setOnClickListener {
            (activity as DashboardActivity?)!!.setCurrentFragment(
                (activity as DashboardActivity?)!!.appointment
            )
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Appointment from home")
            }
        }
        mBinding.cvTwo.setOnClickListener {
            if (!SharedPref.isMember()) {
                showToast("Only members can access the Consolidated Prescription. Please subscribe to become a member")
                navigateTo(SubscriptionActivity::class.java)
            } else if (pdfUrl.length > 1) {
                val mBundle = Bundle()
                mBundle.putString(KEY_WEB_KEY, "PDF")
                mBundle.putString(KEY_WEB_URL, pdfUrl)
                navigateTo(PDFActivity::class.java, mBundle)
            } else
                showToast("Consolidated Prescription is not available for now. Please contact your health coach.")

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Consolidated Prescription from Home")
            }
        }
        mBinding.cvThree.setOnClickListener {
            if (SharedPref.isMember()) (activity as DashboardActivity?)!!.setCurrentFragment(
                    (activity as DashboardActivity?)!!.healthVault)
            else {
                showToast("Please subscribe to become a member")
                navigateTo(SubscriptionActivity::class.java)
            }

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Go to Health vault from home")
            }
        }
    }

    private fun loadHomeGraph() {
        mViewModel.getHome { it, url ->
            mBinding.radioGroup1.setOnCheckedChangeListener { group, i ->
                when (i) {
                    mBinding.radioButton1.id -> bindChart(
                        it.lifetime!!.before_meal!!,
                        "Fasting"
                    )
                    mBinding.radioButton2.id -> bindChart(
                        it.lifetime!!.after_meal!!,
                        "After Meal"
                    )
                    mBinding.radioButton3.id -> bindChart(
                        it.lifetime!!.random!!,
                        "Random"
                    )
                }
            }
            mBinding.radioGroup1.check(mBinding.radioButton1.id)
            bindChart(it.lifetime!!.before_meal!!, "Fasting")
            pdfUrl = url!!
        }
    }

    private fun loadNotification() {
        mViewModel.notifications.observe(this) {
            if (it!!.isNotEmpty()) {
                mBinding.llHide.visibility = View.VISIBLE
                mBinding.viewHide.visibility = View.VISIBLE
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
                    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                        param(
                            FirebaseAnalytics.Param.CONTENT,
                            "Notification item clicked ${string}"
                        )
                    }
                }
                mBinding.vpNotification.adapter = mAdapter
                mBinding.llNotificationIndicator.removeAllViews()
                mBinding.llNotificationIndicator.visibility = View.GONE
                if (it.size > 1) {
                    mBinding.llNotificationIndicator.visibility = View.VISIBLE
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
            } else {

                mBinding.llHide.visibility = View.GONE
                mBinding.viewHide.visibility = View.GONE
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

    private fun showDateDialog() {
        val dialogDateBinding: DialogDateBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.dialog_date,
                null,
                false
            )

        val mDialog = AlertDialog.Builder(requireContext(), 0).create()

        mDialog.apply {
            setView(dialogDateBinding.root)
            setCancelable(true)
        }.show()

        val mCalendar = Calendar.getInstance()
        dialogDateBinding.datePicker.maxDate = mCalendar.timeInMillis

        dateString = "${mCalendar.get(Calendar.YEAR)}-${mCalendar.get(Calendar.MONTH)}-${
            mCalendar.get(Calendar.DAY_OF_MONTH)
        }"
        dialogDateBinding.tvSelectedVal.text = "${displayingDateFormat(dateString)}"
        dialogDateBinding.datePicker.init(
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            val m = month + 1
            dateString = "$year-$m-$day"
            dialogDateBinding.tvSelectedVal.text = "${displayingDateFormat(dateString)}"
        }

        dialogDateBinding.btCancel.setOnClickListener { mDialog.dismiss() }
        dialogDateBinding.btPickDate.setOnClickListener {
            if (dateString.isEmpty()) showToast("Select a valid date")
            else {
                mBinding.tvDate.text = "${displayingDateFormat(dateString)}"
                mDialog.dismiss()
            }
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Date selected")
            }
        }
    }

    private fun showTimeDialog() {
        val dialogTimeBinding: DialogTimeBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.dialog_time,
                null,
                false
            )

        val mDialog = AlertDialog.Builder(requireContext(), 0).create()

        mDialog.apply {
            setView(dialogTimeBinding.root)
            setCancelable(true)
        }.show()

        dialogTimeBinding.timePicker.setOnTimeChangedListener { _, _hour, minute ->
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
            dialogTimeBinding.tvSelectedVal.text = timeString
        }

        dialogTimeBinding.btCancel.setOnClickListener { mDialog.dismiss() }
        dialogTimeBinding.btPickTime.setOnClickListener {
            if (timeString.isEmpty()) showToast("Select a valid time")
            else {
                mBinding.tvTime.text = timeString
                mDialog.dismiss()
            }
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Time selected")
            }
        }
    }

    private fun bindChart(chartData: GraphItems, s: String) {
        mBinding.tvAvg.text = chartData.summary!!.avg.toString()
        mBinding.tvTaget.text =
            "${chartData.summary!!.minTarget}-${chartData.summary!!.maxTarget}"
        mBinding.tvMin.text = chartData.summary!!.min.toString()
        mBinding.tvMax.text = chartData.summary!!.max.toString()
        mBinding.tvHyper.text = chartData.summary!!.incident!!.hyper.toString()
        mBinding.tvHypo.text = chartData.summary!!.incident!!.hypo.toString()
        mBinding.tvGraphTitle.text = "Blood Sugar - $s"

        val months = ArrayList<String>()
        chartData.list!!.reversed().forEach {
            months.add(displayingDayFromAPI(it.measure_date!!)!!)
        }

        if (chartData.summary!!.incident!!.hyper!! > 0 || chartData.summary!!.incident!!.hypo!! > 0)
            mBinding.ivRedBanner.visibility = View.VISIBLE
        else mBinding.ivRedBanner.visibility = View.GONE

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

            mBinding.chart1.drawOrder = arrayOf(CombinedChart.DrawOrder.LINE)
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
            xAxis.position = XAxis.XAxisPosition.BOTTOM
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
            try {
                val data = CombinedData()

                data.setData(generateLineData(chartData))

//            xAxis.axisMaximum = data.xMax + 0.25f

                if (chartData.list!!.size > 1) xAxis.axisMinimum = -0.3f

                mBinding.chart1.animateY(2000, Easing.EaseOutBack)

                mBinding.chart1.data = data
                mBinding.chart1.axisRight.isEnabled = false
                mBinding.chart1.legend.isEnabled = false
                mBinding.chart1.axisLeft.setDrawGridLines(false)
                mBinding.chart1.xAxis.setDrawGridLines(false)
                mBinding.chart1.invalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mBinding.rlGraphContainer.visibility = View.GONE
            mBinding.tvChartPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun generateLineData(chartData: GraphItems): LineData {
        val d = LineData()
        val entries = ArrayList<Entry>()

        val colors = ArrayList<Int>()

        chartData.list!!.reversed().forEachIndexed { i, l ->
            entries.add(Entry(i + 0.0f, l.log_value!! + 0f))
            if (l.status == "hyper" || l.status == "hypo") {
                colors.add(context!!.getColor(R.color.red))
            } else colors.add(context!!.getColor(R.color.blue))
        }

        val set = LineDataSet(entries, "")
        set.color = context!!.getColor(R.color.blueOpacity)
        set.lineWidth = 4f
        set.setCircleColor(context!!.getColor(R.color.blue))
        set.circleRadius = 5f
        set.fillColor = context!!.getColor(R.color.blue)
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

    override fun onResume() {
        super.onResume()
        loadHomeGraph()
        mProfile = SharedPref.getUser()
        Glide.with(this).load(mProfile.pic).placeholder(R.drawable.ic_profile_thumb)
            .into(mBinding.ivProfileImage)
        mBinding.tvName.text = mProfile.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
