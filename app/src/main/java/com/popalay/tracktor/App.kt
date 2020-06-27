package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import com.github.zsoltk.compose.router.Router
import com.popalay.tracktor.list.ListScreen

@Composable
fun App(defaultRouting: Routing) {
    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Tracktor") }
            )
        }
    ) {
        Router("HomeScreen", defaultRouting) { backStack ->
            when (val routing = backStack.last()) {
                is Routing.ListDestination -> ListScreen()
            }
        }
    }
}