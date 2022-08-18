package com.safeguardFamily.diabezone.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.viewModel.BaseViewModel

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layout: Int,
    private val viewModelClass: Class<VM>?
) : AppCompatActivity() {

    protected lateinit var mBinding: VB

    protected lateinit var mViewModel: VM

    protected abstract fun onceCreated()

    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, layout)
        if (viewModelClass != null) mViewModel = ViewModelProvider(this)[viewModelClass]
        mBinding.lifecycleOwner = this
        onceCreated()
        mViewModel.apiError.observe(this) { showToast(it) }
        mViewModel.apiLoader.observe(this) { if (it) showLoading() else hideLoading() }

    }

    fun expand(v: View) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight: Int = v.measuredHeight
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE

        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds() = true
        }
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight: Int = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) =
                if (interpolatedTime == 1f) v.visibility = View.GONE
                else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

            override fun willChangeBounds() = true
        }
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
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

    open fun sendMessage(message: String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")

        intent.putExtra(Intent.EXTRA_TEXT, message)

        if (intent.resolveActivity(packageManager) == null) {
            showToast("Please install whatsapp first.")
            return
        }
        startActivity(intent)
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
            if (dialog != null) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}