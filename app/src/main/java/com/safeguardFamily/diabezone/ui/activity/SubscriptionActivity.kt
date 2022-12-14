package com.safeguardFamily.diabezone.ui.activity

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivitySubscriptionBinding
import com.safeguardFamily.diabezone.model.request.*
import com.safeguardFamily.diabezone.model.response.Info
import com.safeguardFamily.diabezone.model.response.Package
import com.safeguardFamily.diabezone.ui.adapter.ExpandableInfoAdapter
import com.safeguardFamily.diabezone.ui.adapter.LinePagerIndicatorDecoration
import com.safeguardFamily.diabezone.ui.adapter.ProgramsAdapter
import com.safeguardFamily.diabezone.ui.adapter.SubscribeProgramsAdapter
import com.safeguardFamily.diabezone.viewModel.SubscriptionViewModel
import org.json.JSONException
import org.json.JSONObject

class SubscriptionActivity : BaseActivity<ActivitySubscriptionBinding, SubscriptionViewModel>(
    R.layout.activity_subscription,
    SubscriptionViewModel::class.java
), PaymentResultListener {

    private lateinit var pack: Package

    private var currentPosition = 0

    var runnable: Runnable? = null
    val handler = Handler(Looper.getMainLooper())

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


        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, 5000)
            Log.d("RRR -- ", "onceCreated: $currentPosition")
//            mBinding.rvBanner.layoutManager!!.scrollToPosition(currentPosition)
            mBinding.rvBanner.betterSmoothScrollToPosition(currentPosition)
            if (currentPosition == 5) currentPosition = 0
            else currentPosition++
        }.also { runnable = it }, 5000)


        mViewModel.getPrograms {
            loadProgram(it.packages!!)
            loadInfo(it.info!!)
        }
    }

    fun RecyclerView.betterSmoothScrollToPosition(targetItem: Int) {
        layoutManager?.apply {
            val maxScroll = 6
            when (this) {
                is LinearLayoutManager -> {
                    val topItem = findFirstVisibleItemPosition()
                    val distance = topItem - targetItem
                    val anchorItem = when {
                        distance > maxScroll -> targetItem + maxScroll
                        distance < -maxScroll -> targetItem - maxScroll
                        else -> topItem
                    }
                    if (anchorItem != topItem) scrollToPosition(anchorItem)
                    post {
                        smoothScrollToPosition(targetItem)
                    }
                }
                else -> smoothScrollToPosition(targetItem)
            }
        }
    }

    private fun loadInfo(info: List<Info>) {
        mBinding.rvInfo.layoutManager = LinearLayoutManager(this)
        mBinding.rvInfo.adapter = ExpandableInfoAdapter(info)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    private fun loadProgram(packages: List<Package>) {
        mBinding.rvPrograms.adapter = SubscribeProgramsAdapter(packages) { it ->
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Subscription Payment called ${it.pid}")
            }
            pack = it
            mViewModel.getOrderID(
                OrderIdRequest(
                    amount = (pack.programFee!!.toInt() * 100).toString(),
                    pid = pack.pid!!,
                    type = "package",
                    uid = SharedPref.getUserId()!!
                )
            ) { orderId ->
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.PAYMENT_TYPE, "Payment For Subscription with orderId:$orderId")
                }
                Log.d(Bundle.TAG, "onceCreated - ORder ID: $orderId")
                val amount = pack.programFee!!.toInt() * 100
                val checkout = Checkout()
                checkout.setKeyID("rzp_live_LLwJrP6eCuhu9U")
//                checkout.setKeyID("rzp_test_C5aketpmxb6Hl6")

                checkout.setImage(R.mipmap.ic_launcher)
                val obj = JSONObject()
                try {
                    obj.put("name", "SafeGuardFamily")
                    obj.put("description", "Payment for Subscription")
                    obj.put("theme.color", "")
                    obj.put("currency", "INR")
                    obj.put("order_id", orderId)
                    obj.put("amount", amount)
                    obj.put("prefill.contact", SharedPref.getUser().mobile)
                    obj.put("prefill.email", SharedPref.getUser().email)
                    Log.d(Bundle.TAG, "onceCreated - ORder ID: ${Gson().toJson(obj)}")
                    checkout.open(this, obj)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
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