package com.ssafy.firstproject.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommonUtils {

    fun formatDateYYMMDD(millis: Long): String {
        val sdf = SimpleDateFormat("yy-MM-dd", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}