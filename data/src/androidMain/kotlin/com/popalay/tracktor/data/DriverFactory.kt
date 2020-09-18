package com.popalay.tracktor.data

import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.data.extensions.now
import com.popalay.tracktor.data.model.ProgressDirection
import com.popalay.tracktor.data.model.TrackableUnit
import com.popalay.tracktor.db.Tracker
import com.popalay.tracktor.db.TrackerQueries
import com.popalay.tracktor.db.TracktorDatabase
import com.popalay.tracktor.db.ValueRecord
import com.popalay.tracktor.db.ValueRecordQueries
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.concurrent.Executors

actual class DriverFactory : KoinComponent {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        TracktorDatabase.Schema,
        get(),
        "tracktor.db",
        callback = object : AndroidSqliteDriver.Callback(TracktorDatabase.Schema) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadScheduledExecutor().execute {
                    runBlocking {
                        get<TrackerQueries>().insert(
                            Tracker(
                                "id",
                                "Number of cool app installs",
                                ProgressDirection.ASCENDING,
                                LocalDateTime.now(),
                                TrackableUnit.Quantity.name,
                                TrackableUnit.Quantity.symbol,
                                TrackableUnit.Quantity.valueType,
                                false
                            )
                        )
                        get<ValueRecordQueries>().insert(
                            ValueRecord(
                                "id",
                                "id",
                                42.0,
                                "",
                                LocalDateTime.now()
                            )
                        )
                    }
                }
            }
        }
    )
}