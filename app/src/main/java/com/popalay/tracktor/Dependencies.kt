package com.popalay.tracktor

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.data.AppDatabase
import com.popalay.tracktor.data.MIGRATION_1_2
import com.popalay.tracktor.data.MIGRATION_2_3
import com.popalay.tracktor.data.TrackerDao
import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.featureflags.FeatureFlagsManager
import com.popalay.tracktor.data.featureflags.RealFeatureFlagsManager
import com.popalay.tracktor.data.featureflags.RealSmallTrackerListItemFeatureFlag
import com.popalay.tracktor.data.featureflags.SmallTrackerListItemFeatureFlag
import com.popalay.tracktor.domain.formatter.NumberValueRecordFormatter
import com.popalay.tracktor.domain.formatter.StringValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatterFacade
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.ui.list.ListWorkflow
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.time.LocalDateTime
import java.util.concurrent.Executors

val coreModule = module {
    factory { ListWorkflow(get(), get()) }
    factory { TrackerDetailWorkflow(get()) }
    factory { FeatureFlagsListWorkflow(get()) }
}

val domainModule = module {
    single { GetAllTrackersWorker(get()) }
    single { setOf(NumberValueRecordFormatter(), StringValueRecordFormatter()) }
    single<ValueRecordFormatter> { ValueRecordFormatterFacade(get()) }
}

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database-name")
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
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

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
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