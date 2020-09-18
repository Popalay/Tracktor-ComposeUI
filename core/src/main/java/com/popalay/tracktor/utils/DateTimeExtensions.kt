package com.popalay.tracktor.utils

import android.text.format.DateUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

fun LocalDateTime.toRelativeFormat(): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val time = toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    return if (now - time < 60) {
        "just now"
    } else {
        val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_ABBREV_RELATIVE
        DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS, flags).toString()
    }
}