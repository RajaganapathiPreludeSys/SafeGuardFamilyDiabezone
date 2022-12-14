package com.safeguardFamily.diabezone.apiService

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No network connection available"
}