package com.safeguardFamily.diabezone.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.ui.activity.MobileActivity
import com.safeguardFamily.diabezone.viewModel.BaseViewModel

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layout: Int,
    private val viewModelClass: Class<VM>?
) : AppCompatActivity() {

    protected lateinit var mBinding: VB

    lateinit var mViewModel: VM

    protected abstract fun onceCreated()

    private var dialog: Dialog? = null

    private var noNetworkDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, layout)
        if (viewModelClass != null) mViewModel = ViewModelProvider(this)[viewModelClass]
        mBinding.lifecycleOwner = this
        onceCreated()
        mViewModel.apiError.observe(this) {
            Log.d(TAG, "onCreate: $it")
            showToast(it!!)
        }
        mViewModel.successToast.observe(this) { showToast(it) }
        mViewModel.apiLoader.observe(this) { if (it) showLoading() else hideLoading() }
        mViewModel.noInternet.observe(this) { if (it) showNoNetwork() else hideNoNetwork() }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, this.javaClass.toString())
            param(FirebaseAnalytics.Param.SCREEN_CLASS, mViewModel.javaClass.toString())
        }
    }

    open fun navigateTo(className: Class<*>, doFinish: Boolean = false) {
        startActivity(Intent(this, className).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        if (doFinish) finish()
    }

    open fun navigateTo(className: Class<*>, bundle: Bundle, doFinish: Boolean = false) {
        startActivity(
            Intent(this, className)
                .putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        ); if (doFinish) finish()
    }

    open fun showToast(message: String, isShort: Boolean = true) = Toast.makeText(
        this, message, if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()

    open fun showToast(@StringRes resId: Int, isShort: Boolean = true) = Toast.makeText(
        this, getString(resId), if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    open fun loadProfileImg(url: String, imageView: ImageView) {
        Glide.with(this).load(url).into(imageView)
    }

    fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun openWhatsApp(num: String) {
        val appInstalled = try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        if (appInstalled) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=+91$num")
            startActivity(intent)
        } else showToast("WhatsApp not available in you mobile")
    }

    open fun openMail(mail: String, subject: String, text: String) {
        startActivity(Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        })
    }

    open fun showLoading() {
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_loading)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
        try {
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun hideLoading() {
        try {
            if (dialog != null) dialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun showNoNetwork() {
        noNetworkDialog = Dialog(this)
        noNetworkDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        noNetworkDialog!!.setContentView(R.layout.no_network_dialog)
        noNetworkDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        noNetworkDialog!!.setCancelable(false)
        try {
            noNetworkDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun hideNoNetwork() {
        try {
            if (noNetworkDialog != null) noNetworkDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun logout() {
        navigateTo(MobileActivity::class.java)
        finishAffinity()
        SharedPref.clearAll()
    }

    open fun longLog(sb: String) {
        if (sb.length > 4000) {
            Log.d(TAG, "sb.length = " + sb.length)
            val chunkCount: Int = sb.length / 4000 // integer division
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                if (max >= sb.length) {
                    Log.d(TAG, "chunk " + i + " of " + chunkCount + ": " + sb.substring(4000 * i))
                } else {
                    Log.d(
                        TAG,
                        "chunk " + i + " of " + chunkCount + ": " + sb.substring(4000 * i, max)
                    )
                }
            }
        }
    }
}