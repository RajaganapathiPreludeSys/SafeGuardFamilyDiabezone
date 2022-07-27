package com.safeguardFamily.diabezone.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<VB : ViewDataBinding, VM : ViewModel>(
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
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onceCreated()
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

    open fun sendMessage(message: String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")

        intent.putExtra(Intent.EXTRA_TEXT, message)

        if (intent.resolveActivity(requireContext().packageManager) == null) {
            showToast("Please install whatsapp first.")
            return
        }
        startActivity(intent)
    }

}