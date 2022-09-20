package com.safeguardFamily.diabezone.ui.activity

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.ExpandableInfoAdapter
import com.safeguardFamily.diabezone.adapter.LinePagerIndicatorDecoration
import com.safeguardFamily.diabezone.adapter.ProgramsAdapter
import com.safeguardFamily.diabezone.adapter.SubscribeProgramsAdapter
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivitySubscriptionBinding
import com.safeguardFamily.diabezone.model.request.Error
import com.safeguardFamily.diabezone.model.request.PaymentFailRequest
import com.safeguardFamily.diabezone.model.request.PaymentResponse
import com.safeguardFamily.diabezone.model.request.SubscriptionRequest
import com.safeguardFamily.diabezone.model.response.Info
import com.safeguardFamily.diabezone.model.response.Package
import com.safeguardFamily.diabezone.viewModel.SubscriptionViewModel
import org.json.JSONException
import org.json.JSONObject

class SubscriptionActivity : BaseActivity<ActivitySubscriptionBinding, SubscriptionViewModel>(
    R.layout.activity_subscription,
    SubscriptionViewModel::class.java
), PaymentResultListener {

    private lateinit var pack: Package

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.ivBack.setOnClickListener { finish() }

        val s = ArrayList<Int>()
        s.add(R.drawable.s1)
        s.add(R.drawable.s2)
        s.add(R.drawable.s3)
        s.add(R.drawable.s4)
        s.add(R.drawable.s5)
        s.add(R.drawable.s6)

        mBinding.rvBanner.adapter = ProgramsAdapter(s)
        mBinding.rvBanner.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvBanner.setHasFixedSize(true)
        mBinding.rvBanner.addItemDecoration(LinePagerIndicatorDecoration(this))

        mViewModel.getPrograms {
            loadProgram(it.packages!!)
            loadInfo(it.info!!)
        }
    }

    private fun loadInfo(info: List<Info>) {
        mBinding.rvInfo.layoutManager = LinearLayoutManager(this)
        mBinding.rvInfo.adapter = ExpandableInfoAdapter(info)
    }

    private fun loadProgram(packages: List<Package>) {
        mBinding.rvPrograms.adapter = SubscribeProgramsAdapter(packages) {
            pack = it
            val amount = it.programFee!!.toInt() * 100
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_C5aketpmxb6Hl6")
            checkout.setImage(R.mipmap.ic_launcher)
            val obj = JSONObject()
            try {
                obj.put("name", "SafeGuardFamily")
                obj.put("description", "Payment for Subscription")
                obj.put("theme.color", "")
                obj.put("currency", "INR")
                obj.put("amount", amount)
                obj.put("prefill.contact", SharedPref.getUser().mobile)
                obj.put("prefill.email", SharedPref.getUser().email)
                checkout.open(this, obj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        mBinding.rvPrograms.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvPrograms.setHasFixedSize(true)
    }

    override fun onPaymentSuccess(p0: String?) {
        val sub = SubscriptionRequest(
            pid = pack.pid,
            uid = SharedPref.getUserId(),
            paymentResponse = PaymentResponse(key = p0)
        )

        mViewModel.subscribe(sub) {
            mViewModel.getPrograms {
                loadProgram(it.packages!!)
            }
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        showToast("Payment failed, Please retry later")
        Log.d(Bundle.TAG, "Razorpay onPayment Error() called with: p0 = $p0, p1 = $p1")
        mViewModel.payFailed(
            PaymentFailRequest(
                amount = pack.programFee,
                error = Gson().fromJson(p1, Error::class.java),
                pid = pack.pid,
                puid = "",
                selDate = "",
                slot = "",
                type = "package",
                uid = SharedPref.getUserId()
            )
        )

    }

}