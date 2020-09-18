package com.popalay.tracktor.data

import com.popalay.tracktor.db.TracktorDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TracktorDatabase.Schema, "tracktor.db")
    }
}