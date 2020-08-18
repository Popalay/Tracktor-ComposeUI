package com.popalay.tracktor.ui.list

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.state
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.popalay.tracktor.R
import com.popalay.tracktor.data.model.MenuItem
import com.popalay.tracktor.ui.widget.TopAppBar

@Preview
@Composable
fun LogoAppBar(
    menuItems: List<MenuItem> = emptyList(),
    onMenuItemClicked: (MenuItem) -> Unit = {}
) {
    TopAppBar(
        title = {
            Image(
                vectorResource(R.drawable.ic_logo_text),
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        actions = {
            AppBarMenuButton(menuItems, onMenuItemClicked)
        }
    )
}

@Composable
private fun AppBarMenuButton(
    menuItems: List<MenuItem>,
    onMenuItemClicked: (MenuItem) -> Unit
) {
    val showDropDownMenu = state { false }

    DropdownMenu(
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