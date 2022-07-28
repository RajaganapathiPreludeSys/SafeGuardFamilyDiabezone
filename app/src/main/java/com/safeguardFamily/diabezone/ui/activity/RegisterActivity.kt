package com.safeguardFamily.diabezone.ui.activity

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
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.base.BaseActivity
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.databinding.ActivityRegisterBinding
import com.safeguardFamily.diabezone.viewModel.RegisterViewModel
import java.io.File

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(
    R.layout.activity_register,
    RegisterViewModel::class.java
) {
    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        val spanString = SpannableString(getString(R.string.terms_and_conditions))

        val termsAndCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val mBundle = Bundle()
                mBundle.putString(KEY_WEB_KEY, "Teams and Conditions")
                navigateTo(WebViewActivity::class.java, mBundle)
            }
        }

        spanString.setSpan(termsAndCondition, 47, 67, 0)
        spanString.setSpan(ForegroundColorSpan(Color.BLACK), 47, 67, 0)
        spanString.setSpan(UnderlineSpan(), 47, 67, 0)
        spanString.setSpan(StyleSpan(Typeface.BOLD), 47, 67, 0)

        mBinding.cbTermsAndCondition.movementMethod = LinkMovementMethod.getInstance()
        mBinding.cbTermsAndCondition.setText(spanString, TextView.BufferType.SPANNABLE)
        mBinding.cbTermsAndCondition.isSelected = true

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
                    if (p0.toString().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(p0.toString())
                            .matches()
                    ) null else getString(R.string.valid_email)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        mBinding.btRegister.setOnClickListener {
            if (mBinding.tieName.text!!.isEmpty())
                mBinding.tilName.error = getString(R.string.valid_name)
            else if (mBinding.tieEmail.text!!.isEmpty())
                mBinding.tilEmail.error = getString(R.string.valid_email)
            else
                if (mBinding.cbTermsAndCondition.isChecked) {
                    navigateTo(DashboardActivity::class.java)
                } else showToast(R.string.accept_terms)
        }

        val loadImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                it?.let { mBinding.ivProfileImage.setImageURI(it) }
                Log.d("RRR -- ", "onceCreated: $it")
            }

        val tempImageUri = initTempUri()
        val resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            mBinding.ivProfileImage.setImageURI(tempImageUri)
        }

        mBinding.ivProfileImage.setOnClickListener {
            val alertDialog: AlertDialog = this@RegisterActivity.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setMessage("Select an option for profile picture...")
                    setPositiveButton("Pick from gallery") { dialog, id ->
                        loadImage.launch("image/*")
                    }
                    setNegativeButton("Take picture") { dialog, id ->
                        resultLauncher.launch(tempImageUri)
                    }
                }
                builder.create()
            }
            alertDialog.show()
        }
    }

    private fun initTempUri(): Uri {
        val tempImagesDir = File(applicationContext.filesDir, "temp_images")
        tempImagesDir.mkdir()
        return FileProvider.getUriForFile(
            applicationContext,
            "com.example.fileprovider",
            File(tempImagesDir, "image.jpg")
        )
    }
}