package com.popalay.tracktor

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.data.AppDatabase
import com.popalay.tracktor.data.MIGRATION_1_2
import com.popalay.tracktor.data.MIGRATION_2_3
import com.popalay.tracktor.data.MIGRATION_3_4
import com.popalay.tracktor.data.TrackerDao
import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.featureflags.FeatureFlagsManager
import com.popalay.tracktor.data.featureflags.RealFeatureFlagsManager
import com.popalay.tracktor.data.featureflags.RealSmallTrackerListItemFeatureFlag
import com.popalay.tracktor.data.featureflags.SmallTrackerListItemFeatureFlag
import com.popalay.tracktor.domain.formatter.NumberValueRecordFormatter
import com.popalay.tracktor.domain.formatter.TextValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatterFacade
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.popalay.tracktor.domain.worker.GetAllUnitsWorker
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.ui.createtracker.CreateTrackerWorkflow
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.ui.list.ListWorkflow
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.time.LocalDateTime
import java.util.concurrent.Executors

val coreModule = module {
    single { AppWorkflow(get(), get(), get(), get()) }
    single { ListWorkflow(get(), get(), get()) }
    single { TrackerDetailWorkflow(get()) }
    single { FeatureFlagsListWorkflow(get()) }
    single { CreateTrackerWorkflow(get(), get(), get()) }
}

val domainModule = module {
    single { GetAllTrackersWorker(get()) }
    single { GetAllUnitsWorker() }
    single { setOf(NumberValueRecordFormatter(), TextValueRecordFormatter()) }
    single<ValueRecordFormatter> { ValueRecordFormatterFacade(get()) }
}

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database-name")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Executors.newSingleThreadScheduledExecutor().execute {
                        get<TrackerDao>().insertSync(Tracker("id", "title", TrackableUnit.Weight, LocalDateTime.now()))
                    }
                }
            }).build()
    }

    single { get<AppDatabase>().trackerDao() }
    single { get<AppDatabase>().recordDao() }
    single { TrackingRepository(get(), get()) }

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

    single { Moshi.Builder().build() }
}

val featureFlagsModule = module {
    single<SmallTrackerListItemFeatureFlag> { RealSmallTrackerListItemFeatureFlag(get()) }
    single<FeatureFlagsManager> { RealFeatureFlagsManager(get()) }
}

val modules = listOf(
    coreModule,
    domainModule,
    dataModule,
    featureFlagsModule
)