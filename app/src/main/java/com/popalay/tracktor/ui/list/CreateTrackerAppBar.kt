package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.StackScope
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.preferredHeight
import androidx.ui.material.DropdownMenu
import androidx.ui.material.DropdownMenuItem
import androidx.ui.material.IconButton
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.R
import com.popalay.tracktor.model.MenuItem

@Preview
@Composable
fun CreateTrackerAppBar(
    menuItems: List<MenuItem> = emptyList(),
    title: String = "",
    onValueChanged: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onMenuItemClicked: (MenuItem) -> Unit = {}
) {
    TopAppBar(modifier = Modifier.preferredHeight(160.dp)) {
        Column(verticalArrangement = Arrangement.SpaceAround) {
            Spacer(modifier = Modifier.height(16.dp))
            Stack(modifier = Modifier.fillMaxWidth()) {
                AppBarMenuButton(menuItems, onMenuItemClicked)
                Image(
                    vectorResource(R.drawable.ic_logo_text),
                    modifier = Modifier.gravity(Alignment.TopCenter)
                )
            }
            CreateTrackedValue(title, onValueChanged, onSubmit)
        }
    }
}

@Composable
private fun StackScope.AppBarMenuButton(
    menuItems: List<MenuItem>,
    onMenuItemClicked: (MenuItem) -> Unit
) {
    val showDropDownMenu = state { false }

    DropdownMenu(
        toggleModifier = Modifier.Companion.gravity(Alignment.CenterEnd),
        toggle = {
            IconButton(onClick = { showDropDownMenu.value = !showDropDownMenu.value }) {
                Icon(Icons.Default.MoreVert)
            }
        },
        expanded = showDropDownMenu.value,
        onDismissRequest = { showDropDownMenu.value = false }
    ) {
        menuItems.forEach {
            DropdownMenuItem(onClick = { onMenuItemClicked(it) }) {
                Text(it.displayName)
            }
        }
    }
}