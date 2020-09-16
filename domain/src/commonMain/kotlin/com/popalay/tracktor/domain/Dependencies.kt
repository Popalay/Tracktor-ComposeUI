package com.popalay.tracktor.domain

import com.popalay.tracktor.domain.formatter.NumberValueRecordFormatter
import com.popalay.tracktor.domain.formatter.TextValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.domain.formatter.ValueRecordFormatterFacade
import com.popalay.tracktor.domain.worker.GetAllCategoriesWorker
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.popalay.tracktor.domain.worker.GetAllUnitsWorker
import com.popalay.tracktor.domain.workflow.AppWorkflow
import com.popalay.tracktor.domain.workflow.CreateTrackerWorkflow
import com.popalay.tracktor.domain.workflow.FeatureFlagsListWorkflow
import com.popalay.tracktor.domain.workflow.ListWorkflow
import com.popalay.tracktor.domain.workflow.SettingsWorkflow
import com.popalay.tracktor.domain.workflow.TrackerDetailWorkflow
import org.koin.dsl.module

val domainModule = module {
    single { GetAllTrackersWorker(get()) }
    single { GetAllUnitsWorker() }
    single { GetAllCategoriesWorker(get()) }
    single { setOf(NumberValueRecordFormatter(), TextValueRecordFormatter()) }
    single<ValueRecordFormatter> { ValueRecordFormatterFacade(get()) }
    single { AppWorkflow(get()) }
    single { ListWorkflow(get(), get(), get(), get(), get()) }
    single { TrackerDetailWorkflow(get(), get(), get()) }
    single { FeatureFlagsListWorkflow() }
    single { CreateTrackerWorkflow(get(), get()) }
    single { SettingsWorkflow(get()) }
}