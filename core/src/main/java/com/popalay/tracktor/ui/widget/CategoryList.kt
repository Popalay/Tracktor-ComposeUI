package com.popalay.tracktor.ui.widget

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.ui.dialog.AddCategoryDialog

@Composable
fun TrackerCategoryList(
    availableCategories: List<Category>,
    categories: List<Category>,
    isAddCategoryDialogShowing: Boolean = false,
    onSave: (List<Category>) -> Unit = {},
    onAddCategoryClicked: () -> Unit = {},
    onDialogDismissed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (isAddCategoryDialogShowing) {
        AddCategoryDialog(
            categories,
            availableCategories.minus(Category.All),
            onDismissRequest = onDialogDismissed,
            onSave = { onSave(it.toList()) }
        )
    }

    ScrollableRow(modifier) {
        Spacer(modifier = Modifier.width(16.dp))
        Chip(
            onClick = { onAddCategoryClicked() },
            activeColor = MaterialTheme.colors.surface,
            contentColor = contentColorFor(MaterialTheme.colors.surface)
        ) {
            Icon(Icons.Default.Add)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.create_tracker_add_category))
        }
        categories.forEach {
            Spacer(modifier = Modifier.width(8.dp))
            Chip(isSelected = true) {
                Text(text = it.name)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun AllCategoryList(
    categories: List<Category>,
    selectedCategory: Category,
    onCategoryClick: (Category) -> Unit = {},
    modifier: Modifier = Modifier
) {
    ScrollableRow(modifier) {
        Spacer(modifier = Modifier.width(8.dp))
        categories.forEach {
            val contentColor = if (it == selectedCategory) {
                MaterialTheme.colors.secondary
            } else {
                EmphasisAmbient.current.disabled.applyEmphasis(MaterialTheme.colors.secondary)
            }
            Chip(
                isSelected = it == selectedCategory,
                activeColor = Color.Transparent,
                inactiveColor = Color.Transparent,
                contentColor = contentColor,
                onClick = { onCategoryClick(it) }
            ) {
                Text(text = it.name)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}