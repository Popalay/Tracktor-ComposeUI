package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.github.zsoltk.compose.router.Router

@Composable
fun HomeScreen(defaultRouting: Routing) {
    Router("HomeScreen", defaultRouting) { backStack ->
        when (val routing = backStack.last()) {
            is Routing.ListDestination -> Text(text = "List")
        }
    }
}