package com.popalay.tracktor.data

import com.popalay.tracktor.data.converter.LocalDateTimeConverter
import com.popalay.tracktor.db.Tracker
import com.popalay.tracktor.db.TracktorDatabase
import com.popalay.tracktor.db.ValueRecord
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory() {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): TracktorDatabase {
    val driver = driverFactory.createDriver()
    return TracktorDatabase(
        driver,
        Tracker.Adapter(
            dateAdapter = LocalDateTimeConverter(),
            directionAdapter = EnumColumnAdapter(),
            unitValueTypeAdapter = EnumColumnAdapter()
        ),
        ValueRecord.Adapter(
            dateAdapter = LocalDateTimeConverter()
        )
    )
}