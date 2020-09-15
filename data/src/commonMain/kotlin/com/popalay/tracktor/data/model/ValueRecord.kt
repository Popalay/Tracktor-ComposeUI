package com.popalay.tracktor.data.model

import kotlinx.datetime.LocalDateTime

data class ValueRecord(
    val id: String,
    val trackerId: String,
    val value: Double,
    val stringValue: String,
    val date: LocalDateTime
)