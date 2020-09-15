package com.popalay.tracktor

import com.popalay.tracktor.data.dataModule
import com.popalay.tracktor.domain.domainModule
import com.popalay.tracktor.feature.createtracker.CreateTrackerWorkflow
import com.popalay.tracktor.feature.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.feature.list.ListWorkflow
import com.popalay.tracktor.feature.settings.SettingsWorkflow
import com.popalay.tracktor.feature.trackerdetail.TrackerDetailWorkflow
import org.koin.dsl.module

val coreModule = module {
    single { AppWorkflow(get()) }
    single { ListWorkflow(get(), get(), get(), get(), get()) }
    single { TrackerDetailWorkflow(get(), get(), get()) }
    single { FeatureFlagsListWorkflow() }
    single { CreateTrackerWorkflow(get(), get()) }
    single { SettingsWorkflow(get()) }
}

val modules = listOf(
    coreModule,
    domainModule,
    dataModule,
)