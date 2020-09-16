package com.popalay.tracktor

import com.popalay.tracktor.data.dataModule
import com.popalay.tracktor.domain.domainModule
import org.koin.dsl.module

val coreModule = module {
}

val modules = listOf(
    coreModule,
    domainModule,
    dataModule,
)