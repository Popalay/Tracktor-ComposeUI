package com.popalay.tracktor.list

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.unit.dp
import com.popalay.tracktor.create.CreateTrackedValue
import com.popalay.tracktor.model.TrackedValue

@Composable
fun ListScreen() {
    val (currentList, updateList) = state { listOf<Any>("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        AdapterList(data = currentList) {
            if (it is TrackedValue) {
                Spacer(modifier = Modifier.height(8.dp))
                TrackedValueValueListItem(it)
            } else {
                CreateTrackedValue { newValue ->
                    updateList(currentList.plus(newValue))
                }
            }
        }
    }
}