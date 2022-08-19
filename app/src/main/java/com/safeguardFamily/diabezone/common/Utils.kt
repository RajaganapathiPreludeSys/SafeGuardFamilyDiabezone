package com.safeguardFamily.diabezone.common

import com.safeguardFamily.diabezone.common.Bundle.API_DATE_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.DATE_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.FROM_API_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.date12Format
import com.safeguardFamily.diabezone.common.Bundle.date24Format
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatDate(millis: Long): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(millis)
    }

    fun getTimeStampFromSting(s: String): Long {
        val date = SimpleDateFormat(FROM_API_FORMAT, Locale.getDefault()).parse(s)
        return date.time
    }

    fun fromAPIFormat(time: String): String? {
        return SimpleDateFormat(FROM_API_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(it) }
    }

    fun displayingDateFormat(time: String): String? {
        return SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(it) }
    }

    fun apiDateFormat(millis: Long): String {
        return SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).format(millis)
    }

    fun formatTo12Hrs(time: String): String? {
        return SimpleDateFormat(date24Format, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(date12Format, Locale.getDefault()).format(it) }
    }

    fun formatTo24Hrs(time: String): String? {
        return SimpleDateFormat(date12Format, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(date24Format, Locale.getDefault()).format(it) }
    }
}