package com.safeguardFamily.diabezone.ui.activity

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormat
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateTimeFormat
import com.safeguardFamily.diabezone.databinding.ActivityBookingDetailsBinding
import com.safeguardFamily.diabezone.model.response.ProfileResponse
import com.safeguardFamily.diabezone.viewModel.BookingDetailsViewModel

class BookingDetailsActivity : BaseActivity<ActivityBookingDetailsBinding, BookingDetailsViewModel>(
    R.layout.activity_booking_details,
    BookingDetailsViewModel::class.java
) {

    private lateinit var userResponse: ProfileResponse

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }

        if (intent.extras?.containsKey(Bundle.KEY_BOOKING_DETAILS) == true) {
            userResponse = Gson().fromJson(
                intent.extras?.getString(Bundle.KEY_BOOKING_DETAILS),
                ProfileResponse::class.java
            )
            mBinding.profile = userResponse
        }

        val mobileArray = arrayListOf<String>()

        userResponse.past_appointments!!.forEach {
            mobileArray.add("${it.provider!!.name} ${displayingDateTimeFormat(it.booking_date!!)}")
        }

        mBinding.tvMemberShip.text = "Member - ${userResponse.membership!![0].pack_name}"
        mBinding.tvPackValid.text = getString(
            R.string.package_valid,
            displayingDateFormat(userResponse.membership!![0].validity)
        )

        mBinding.lvHistory.adapter = ArrayAdapter(this, R.layout.item_history, mobileArray)

        getListViewSize(mBinding.lvHistory)

        mBinding.rlDoctorContainer.setOnClickListener {
            val bundle = android.os.Bundle()
            bundle.putString(Bundle.KEY_DOCTOR, Gson().toJson(userResponse.health_coach))
            bundle.putString(Bundle.KEY_TITLE, "Your Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)
        }

    }

    fun getListViewSize(myListView: ListView) {
        val myListAdapter: ListAdapter = myListView.adapter ?: return
        var totalHeight = 0
        for (size in 0 until myListAdapter.count) {
            val listItem: View = myListAdapter.getView(size, null, myListView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = myListView.layoutParams
        params.height = totalHeight + myListView.dividerHeight * (myListAdapter.count - 1)
        myListView.layoutParams = params
        Log.i("height of listItem:", totalHeight.toString())
    }
}