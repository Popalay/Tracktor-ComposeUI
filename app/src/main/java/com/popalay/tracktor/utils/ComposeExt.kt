package com.popalay.tracktor.utils

import androidx.compose.Composable
import androidx.compose.remember
import org.koin.core.context.KoinContextHandler
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

@Composable
inline fun <reified T> inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = remember {
    val context = KoinContextHandler.get()
    context.inject(qualifier, parameters)
}