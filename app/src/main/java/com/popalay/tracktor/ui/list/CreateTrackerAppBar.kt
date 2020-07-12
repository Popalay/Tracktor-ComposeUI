package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.layout.preferredHeight
import androidx.ui.material.TopAppBar
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.R

@Preview
@Composable
fun CreateTrackerAppBar(onSubmit: (String) -> Unit = {}) {
    TopAppBar(modifier = Modifier.preferredHeight(140.dp)) {
        Column(verticalArrangement = Arrangement.SpaceAround) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                vectorResource(R.drawable.ic_logo_text),
                modifier = Modifier.gravity(Alignment.CenterHorizontally)
            )
            CreateTrackedValue(onSubmit = onSubmit)
        }
    }
}