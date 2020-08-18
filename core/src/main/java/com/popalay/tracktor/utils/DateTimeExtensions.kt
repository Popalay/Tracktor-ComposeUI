package com.popalay.tracktor.utils

import android.text.format.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toRelativeFormat(): String =
    DateUtils.getRelativeTimeSpanString(atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).toString()