package com.popalay.tracktor.data

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database-name")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .addCallback(DatabaseCallback()).build()
    }

    single { get<AppDatabase>().trackerDao() }
    single { get<AppDatabase>().recordDao() }
    single { get<AppDatabase>().categoryDao() }

    single { TrackingRepository(get(), get(), get()) }
    single { CategoryRepository(get()) }

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

    single { Moshi.Builder().build() }
}