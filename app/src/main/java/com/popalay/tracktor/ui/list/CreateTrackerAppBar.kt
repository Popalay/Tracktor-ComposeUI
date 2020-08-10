package com.popalay.tracktor.ui.list

import androidx.compose.animation.animate
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.popalay.tracktor.R
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.model.MenuItem

@Preview
@Composable
fun LogoAppBar(
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