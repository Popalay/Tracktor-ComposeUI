package com.popalay.tracktor

import com.popalay.tracktor.data.dataModule
import com.popalay.tracktor.data.featureflags.FeatureFlagsManager
import com.popalay.tracktor.data.featureflags.RealFeatureFlagsManager
import com.popalay.tracktor.domain.domainModule
import com.popalay.tracktor.ui.createtracker.CreateTrackerWorkflow
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.ui.list.ListWorkflow
import com.popalay.tracktor.ui.settings.SettingsWorkflow
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow
import org.koin.dsl.module

val coreModule = module {
    single { AppWorkflow(get(), get(), get(), get(), get()) }
    single { ListWorkflow(get(), get(), get()) }
    single { TrackerDetailWorkflow(get(), get(), get()) }
    single { FeatureFlagsListWorkflow(get()) }
    single { CreateTrackerWorkflow(get(), get(), get()) }
    single { SettingsWorkflow() }
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