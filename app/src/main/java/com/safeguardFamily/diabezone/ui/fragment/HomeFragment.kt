package com.safeguardFamily.diabezone.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
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
import com.safeguardFamily.diabezone.model.DoctorModel
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest
import com.safeguardFamily.diabezone.ui.activity.DashboardActivity
import com.safeguardFamily.diabezone.ui.activity.LogBookActivity
import com.safeguardFamily.diabezone.ui.graph.draw.data.InputData
import com.safeguardFamily.diabezone.viewModel.HomeViewModel
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment :
    BaseFragment<com.safeguardFamily.diabezone.databinding.FragmentHomeBinding, HomeViewModel>(
        R.layout.fragment_home,
        HomeViewModel::class.java
    ) {

    private var formatedDate = ""

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.charView.setData(createChartData())
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
                        measureDate = formatedDate,
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

    }

    private fun loadNotification() {

        val json = try {
            val inputStream = requireContext().assets.open("docter.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use { it.read(buffer) }
            String(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            ""
        }

        val list: List<DoctorModel> = Gson()
            .fromJson(json, Array<DoctorModel>::class.java).toList()

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
        ) { view, year, month, day ->
            val _month = month + 1
            dateString = "$year-$_month-$day"
            dialogBinding.tvSelectedVal.text = "${displayingDateFormat(dateString)}$timeString"
            timeValidator.set(year, _month, day)
        }

        dialogBinding.timePicker.setOnTimeChangedListener { tp, _hour, minute ->
            var hour = _hour
            var am_pm = ""
            when {
                hour == 0 -> {
                    hour += 12
                    am_pm = "AM"
                }
                hour == 12 -> am_pm = "PM"
                hour > 12 -> {
                    hour -= 12
                    am_pm = "PM"
                }
                else -> am_pm = "AM"
            }
            val hourString = if (hour < 10) "0$hour" else hour
            val min = if (minute < 10) "0$minute" else minute
            timeString = " $hourString:$min $am_pm"
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
                formatedDate =
                    displayingDateTimeFormatToAPIFormat("${displayingDateFormat(dateString)}$timeString")!!
                Log.d(TAG, "showDateTimeDialog formatedDate: $formatedDate")
                mDialog.dismiss()
            }
        }
    }

    private fun createChartData(): List<InputData> {
        val dataList = ArrayList<InputData>()
        dataList.add(InputData(110))
        dataList.add(InputData(25))
        dataList.add(InputData(0))
        dataList.add(InputData(200))
        dataList.add(InputData(20))
        dataList.add(InputData(80))
        dataList.add(InputData(40))

        var currMillis = System.currentTimeMillis()
        currMillis -= currMillis % TimeUnit.DAYS.toMillis(1)
        for (i in dataList.indices) {
            val position = (dataList.size - 1 - i).toLong()
            val offsetMillis = TimeUnit.DAYS.toMillis(position)
            val millis = currMillis - offsetMillis
            dataList[i].millis = millis
        }
        return dataList
    }
}