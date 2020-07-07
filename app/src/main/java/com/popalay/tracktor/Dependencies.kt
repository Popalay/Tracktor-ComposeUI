package com.popalay.tracktor

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.data.AppDatabase
import com.popalay.tracktor.data.MIGRATION_1_2
import com.popalay.tracktor.data.TrackerDao
import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.ui.list.ListWorkflow
import com.popalay.tracktor.worker.GetAllTrackersWorker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.time.LocalDateTime
import java.util.concurrent.Executors

val coreModule = module {
    factory { ListWorkflow(get(), get()) }
}

val domainModule = module {
    single { GetAllTrackersWorker(get()) }
}

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database-name")
            .addMigrations(MIGRATION_1_2)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Executors.newSingleThreadScheduledExecutor().execute {
                        get<TrackerDao>().insertSync(Tracker("id", "title", TrackableUnit.Kilograms, LocalDateTime.now()))
                    }
                }
            }).build()
    }

    single { get<AppDatabase>().trackerDao() }
    single { get<AppDatabase>().recordDao() }
    single { TrackingRepository(get(), get()) }
}

val modules = listOf(
    coreModule,
    domainModule,
    dataModule
)