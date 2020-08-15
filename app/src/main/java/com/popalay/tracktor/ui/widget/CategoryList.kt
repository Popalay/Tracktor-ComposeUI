package com.popalay.tracktor.ui.widget

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.model.Category
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
            onCloseRequest = onDialogDismissed,
            onSave = { onSave(it.toList()) }
        )
    }

    ScrollableRow(modifier) {
        Spacer(modifier = Modifier.width(16.dp))
        Chip(
            onClick = { onAddCategoryClicked() },
            activeColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        ) {
            Icon(Icons.Default.Add)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Categories")
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
            Spacer(modifier = Modifier.width(8.dp))
            Chip(
                isSelected = it == selectedCategory,
                onClick = { onCategoryClick(it) }
            ) {
                Text(text = it.name)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}