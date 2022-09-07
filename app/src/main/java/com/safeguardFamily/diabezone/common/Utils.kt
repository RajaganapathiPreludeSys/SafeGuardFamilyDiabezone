package com.safeguardFamily.diabezone.common

import com.safeguardFamily.diabezone.common.Bundle.API_DATE_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.API_DATE_TIME_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.DATE_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.DATE_TIME_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.DAY_FORMAT
import com.safeguardFamily.diabezone.common.Bundle.date12Format
import com.safeguardFamily.diabezone.common.Bundle.date24Format
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun displayingDateFromTimeStamp(millis: Long): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(millis)
    }

    fun getTimeStampFromSting(s: String) =
        SimpleDateFormat(API_DATE_TIME_FORMAT, Locale.getDefault()).parse(s)!!.time

    fun displayingDateFromAPI(time: String): String? =
        SimpleDateFormat(API_DATE_TIME_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(it) }

    fun displayingTimeFromAPI(time: String): String? =
        SimpleDateFormat(API_DATE_TIME_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(date12Format, Locale.getDefault()).format(it) }

    fun displayingDayFromAPI(time: String): String? =
        SimpleDateFormat(API_DATE_TIME_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(DAY_FORMAT, Locale.getDefault()).format(it) }

    fun displayingDateFormat(time: String): String? =
        SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(it) }

    fun displayingDateTimeFormat(time: String): String? =
        SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(it) }

    fun displayingTimeFormat(time: String): String? =
        SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(date12Format, Locale.getDefault()).format(it) }

    fun displayingDateTimeFormatToAPIFormat(time: String): String? =
        SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(API_DATE_TIME_FORMAT, Locale.getDefault()).format(it) }

    fun apiDateFormat(millis: Long): String =
        SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).format(millis)

    fun splitDate(time: String): String? =
        SimpleDateFormat(API_DATE_TIME_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).format(it) }

    fun splitTime(time: String): String? =
        SimpleDateFormat(API_DATE_TIME_FORMAT, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(date24Format, Locale.getDefault()).format(it) }

    fun formatTo12Hrs(time: String): String? =
        SimpleDateFormat(date24Format, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(date12Format, Locale.getDefault()).format(it) }

    fun formatTo24Hrs(time: String): String? =
        SimpleDateFormat(date12Format, Locale.getDefault()).parse(time)
            ?.let { SimpleDateFormat(date24Format, Locale.getDefault()).format(it) }
}