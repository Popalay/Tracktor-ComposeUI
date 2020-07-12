package com.popalay.tracktor.utils

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.remember
import androidx.ui.core.ContextAmbient
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

@Composable
inline fun onBackPressed(crossinline action: () -> Unit) {
    val activity = ContextAmbient.current as AppCompatActivity
    onActive {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                action()
            }
        }
        activity.onBackPressedDispatcher.addCallback(callback)
        onDispose { callback.isEnabled = false }
    }
}
