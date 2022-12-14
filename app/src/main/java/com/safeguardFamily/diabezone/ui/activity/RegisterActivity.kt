package com.safeguardFamily.diabezone.ui.activity

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle.KEY_EDIT_PROFILE
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_URL
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.Bundle.URL_TERMS
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.ActivityRegisterBinding
import com.safeguardFamily.diabezone.viewModel.RegisterViewModel
import java.io.File

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(
    R.layout.activity_register,
    RegisterViewModel::class.java
) {

    private val user = SharedPref.getUser()

    private var isEditProfile = false

    private var tempImageUri: Uri? = null

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mBinding.icHeader.ivBack.setOnClickListener { finish() }

        if (intent.extras?.containsKey(KEY_EDIT_PROFILE) == true)
            isEditProfile = intent.extras?.getBoolean(KEY_EDIT_PROFILE)!!

        if (!isEditProfile) {
            val spanString = SpannableString(getString(R.string.terms_and_conditions))

            val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val mBundle = Bundle()
                    mBundle.putString(KEY_WEB_KEY, "Terms and Service")
                    mBundle.putString(KEY_WEB_URL, URL_TERMS)
                    navigateTo(WebViewActivity::class.java, mBundle)
                }
            }

            spanString.setSpan(termsAndCondition, 47, 67, 0)
            spanString.setSpan(ForegroundColorSpan(Color.BLACK), 47, 67, 0)
            spanString.setSpan(StyleSpan(Typeface.BOLD), 47, 67, 0)
            spanString.setSpan(ForegroundColorSpan(getColor(R.color.blue)), 47, 67, 0)

            mBinding.cbTermsAndCondition.movementMethod = LinkMovementMethod.getInstance()
            mBinding.cbTermsAndCondition.setText(spanString, TextView.BufferType.SPANNABLE)
            mBinding.cbTermsAndCondition.isSelected = true
        } else {
            mBinding.tvWelcomeDesc.visibility = View.GONE
            mBinding.cbTermsAndCondition.visibility = View.GONE
            mBinding.icHeader.ivBack.visibility = View.VISIBLE
            mBinding.btRegister.text = "Update User"
            mBinding.icHeader.tvTitle.text = "Update User Profile"
            mBinding.tieName.text = Editable.Factory.getInstance().newEditable(user.name)
            mBinding.tieEmail.text = Editable.Factory.getInstance().newEditable(user.email)
            Glide.with(this).load(user.pic).placeholder(R.drawable.ic_profile_thumb)
                .into(mBinding.ivProfileImage)
        }

        mBinding.tieName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.tilName.error =
                    if (p0.toString().matches(Regex("^[A-Za-z ]+$"))) null
                    else getString(R.string.valid_name)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        mBinding.tieEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.tilEmail.error =
                    if (p0.toString().isNotEmpty()
                        && Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()
                    ) null else getString(R.string.valid_email)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        mBinding.btRegister.setOnClickListener {
            Log.d(TAG, "onceCreated: ${mBinding.tieName.text}")
            if (!mBinding.tieName.text!!.matches(Regex("^[A-Za-z ]+$")))
                showToast(getString(R.string.valid_name))
            else if (mBinding.tieEmail.text!!.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(mBinding.tieEmail.text!!).matches()
            ) showToast(getString(R.string.valid_email))
            else {
                if (isEditProfile) {
                    user.name = mBinding.tieName.text.toString()
                    user.email = mBinding.tieEmail.text.toString()
                    if (tempImageUri != null && tempImageUri!!.path!!.length > 2) {
//                        Log.d(TAG, "getRealPath: ${RealPathUtil.getRealPath(this, tempImageUri)!!}")
//                        val f = File(RealPathUtil.getRealPath(this, tempImageUri)!!)
                        mViewModel.multiPartUser(user, File(tempImageUri!!.path!!)) {
                            showToast("Profile Updated Successfully")
                            tempImageUri = null
                        }
                    } else mViewModel.updateUser(user) {
                        showToast("Profile Updated Successfully")
                    }
                } else if (mBinding.cbTermsAndCondition.isChecked) {
                    user.name = mBinding.tieName.text.toString()
                    user.email = mBinding.tieEmail.text.toString()
                    if (tempImageUri != null && tempImageUri!!.path!!.length > 2) {
                        mViewModel.multiPartUser(user, File(tempImageUri!!.path!!)) {
                            navigateTo(DashboardActivity::class.java)
                            finishAffinity()
                        }
                    } else mViewModel.updateUser(user) {
                        navigateTo(DashboardActivity::class.java)
                        finishAffinity()
                    }
                } else showToast(R.string.accept_terms)
            }
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Register")
            }
        }

        mBinding.ivProfileImage.setOnClickListener {
            ImagePicker.with(this)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .crop(16f, 16f)
                .createIntentFromDialog { launcher.launch(it) }

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Pick profile image")
            }
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                tempImageUri = it.data?.data!!
                mBinding.ivProfileImage.setImageURI(tempImageUri)
            }
        }

}