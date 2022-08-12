package com.safeguardFamily.diabezone.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val apiError = MutableLiveData<String>()
}