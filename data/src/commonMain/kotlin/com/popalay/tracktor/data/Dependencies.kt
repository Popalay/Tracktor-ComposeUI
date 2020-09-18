package com.popalay.tracktor.data

import com.popalay.tracktor.db.TracktorDatabase
import org.koin.dsl.module

val dataModule = module {
    single { createDatabase(DriverFactory()) }

    single { get<TracktorDatabase>().trackerQueries }
    single { get<TracktorDatabase>().valueRecordQueries }
    single { get<TracktorDatabase>().categoryQueries }
    single { get<TracktorDatabase>().categoryTrackerRefQueries }

    single { TrackingRepository(get(), get(), get()) }
    single { CategoryRepository(get(), get()) }
}