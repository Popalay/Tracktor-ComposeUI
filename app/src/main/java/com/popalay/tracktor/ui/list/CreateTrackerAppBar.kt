package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.animation.animate
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.layout.RowScope
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
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
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.model.MenuItem

@Preview
@Composable
fun CreateTrackerAppBar(
    menuItems: List<MenuItem> = emptyList(),
    onMenuItemClicked: (MenuItem) -> Unit = {}
) {
    val animatedHeight = animate(WindowInsetsAmbient.current.top)

    TopAppBar(modifier = Modifier.preferredHeight(animatedHeight + 72.dp)) {
        Image(
            vectorResource(R.drawable.ic_logo_text),
            modifier = Modifier
                .gravity(Alignment.CenterVertically)
                .padding(start = 16.dp, top = WindowInsetsAmbient.current.top)
        )
        Spacer(modifier = Modifier.weight(1F))
        AppBarMenuButton(menuItems, onMenuItemClicked)
    }
}

@Composable
private fun RowScope.AppBarMenuButton(
    menuItems: List<MenuItem>,
    onMenuItemClicked: (MenuItem) -> Unit
) {
    val showDropDownMenu = state { false }

    DropdownMenu(
        toggleModifier = Modifier
            .gravity(Alignment.CenterVertically)
            .padding(top = WindowInsetsAmbient.current.top),
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