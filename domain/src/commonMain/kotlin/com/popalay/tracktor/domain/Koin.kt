package com.popalay.tracktor.domain

import com.popalay.tracktor.data.dataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(domainModule, dataModule)
}

// called by iOS etc
fun initKoin() = initKoin {}