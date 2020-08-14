package com.popalay.tracktor

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.popalay.tracktor.data.AppDatabase
import com.popalay.tracktor.data.CategoryRepository
import com.popalay.tracktor.data.DatabaseCallback
import com.popalay.tracktor.data.MIGRATION_1_2
import com.popalay.tracktor.data.MIGRATION_2_3
import com.popalay.tracktor.data.MIGRATION_3_4
import com.popalay.tracktor.data.MIGRATION_4_5
import com.popalay.tracktor.data.MIGRATION_5_6
import com.popalay.tracktor.data.MIGRATION_6_7
import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.featureflags.FeatureFlagsManager
import com.popalay.tracktor.data.featureflags.RealFeatureFlagsManager
import com.popalay.tracktor.domain.GetAllCategoriesWorker
import com.popalay.tracktor.domain.formatter.NumberValueRecordFormatter
import com.popalay.tracktor.domain.formatter.TextValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatterFacade
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.popalay.tracktor.domain.worker.GetAllUnitsWorker
import com.popalay.tracktor.ui.createtracker.CreateTrackerWorkflow
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.ui.list.ListWorkflow
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { AppWorkflow(get(), get(), get(), get()) }
    single { ListWorkflow(get(), get(), get()) }
    single { TrackerDetailWorkflow(get(), get(), get()) }
    single { FeatureFlagsListWorkflow(get()) }
    single { CreateTrackerWorkflow(get(), get(), get()) }
}

val domainModule = module {
    single { GetAllTrackersWorker(get()) }
    single { GetAllUnitsWorker() }
    single { GetAllCategoriesWorker(get()) }
    single { setOf(NumberValueRecordFormatter(), TextValueRecordFormatter()) }
    single<ValueRecordFormatter> { ValueRecordFormatterFacade(get()) }
}

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database-name")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .addCallback(DatabaseCallback()).build()
    }

    single { get<AppDatabase>().trackerDao() }
    single { get<AppDatabase>().recordDao() }
    single { get<AppDatabase>().categoryDao() }

    single { TrackingRepository(get(), get()) }
    single { CategoryRepository(get()) }

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

    single { Moshi.Builder().build() }
}

val featureFlagsModule = module {
    single<FeatureFlagsManager> { RealFeatureFlagsManager() }
}

val modules = listOf(
    coreModule,
    domainModule,
    dataModule,
    featureFlagsModule
)