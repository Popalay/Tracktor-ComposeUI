package com.popalay.tracktor.utils

import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toRelativeFormat(): String =
    if (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - this.toEpochSecond(ZoneOffset.UTC) < 60) {
        "just now"
    } else {
        val time = atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val now = Instant.now().toEpochMilli()
        val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_ABBREV_RELATIVE
        DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS, flags).toString()
    }