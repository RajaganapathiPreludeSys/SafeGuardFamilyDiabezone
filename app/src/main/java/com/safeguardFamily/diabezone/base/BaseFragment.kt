package com.safeguardFamily.diabezone.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.viewModel.BaseViewModel

abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layout: Int,
    private val viewModelClass: Class<VM>?
) : Fragment() {

    protected lateinit var mBinding: VB

    protected lateinit var mViewModel: VM

    protected abstract fun onceCreated()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, layout, container, false)
        if (viewModelClass != null) mViewModel = ViewModelProvider(this)[viewModelClass]
//        mBinding.lifecycleOwner = this
        mBinding.lifecycleOwner = viewLifecycleOwner
        onceCreated()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.apiError.observe(viewLifecycleOwner) { showToast(it!!) }
        mViewModel.successToast.observe(viewLifecycleOwner) { showToast(it) }
        mViewModel.apiLoader.observe(viewLifecycleOwner) { if (it) showLoading() else hideLoading() }
        mViewModel.noInternet.observe(viewLifecycleOwner) { if (it) showNoNetwork() else hideNoNetwork() }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, this.javaClass.toString())
            param(FirebaseAnalytics.Param.SCREEN_CLASS, mViewModel.javaClass.toString())
        }
    }

    open fun showToast(message: String, isShort: Boolean = true) = Toast.makeText(
        context, message, if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()

    open fun showToast(@StringRes resId: Int, isShort: Boolean = true) = Toast.makeText(
        context, getString(resId), if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()

    open fun showToastA(@StringRes resId: Int, isShort: Boolean = true) {
        (activity as BaseActivity<*, *>).showToast(resId, isShort)
    }

    open fun navigateTo(className: Class<*>, doFinish: Boolean = false) {
        startActivity(Intent(context, className).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        if (doFinish) requireActivity().finish()
    }

    open fun navigateTo(className: Class<*>, bundle: Bundle, doFinish: Boolean = false) {
        startActivity(
            Intent(context, className).putExtras(bundle)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        )
        if (doFinish) requireActivity().finish()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun loadProfileImg(url: String, imageView: ImageView) =
        (activity as BaseActivity<*, *>).loadProfileImg(url, imageView)

    open fun openWhatsApp(num: String) = (activity as BaseActivity<*, *>).openWhatsApp(num)

    open fun openMail(mail: String, subject: String, text: String) =
        (activity as BaseActivity<*, *>).openMail(mail, subject, text)

    open fun showLoading() = (activity as BaseActivity<*, *>).showLoading()
    open fun hideLoading() = (activity as BaseActivity<*, *>).hideLoading()
    open fun showNoNetwork() = (activity as BaseActivity<*, *>).showNoNetwork()
    open fun hideNoNetwork() = (activity as BaseActivity<*, *>).hideNoNetwork()
    open fun longLog(sb: String) = (activity as BaseActivity<*, *>).longLog(sb)
    open fun logout() = (activity as BaseActivity<*, *>).logout()


}