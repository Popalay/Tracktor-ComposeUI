package com.popalay.tracktor.utils

import android.text.format.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toRelativeFormat(): String =
    if (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - this.toEpochSecond(ZoneOffset.UTC) < 60) {
        "just now"
    } else {
        DateUtils.getRelativeTimeSpanString(atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).toString()
    }