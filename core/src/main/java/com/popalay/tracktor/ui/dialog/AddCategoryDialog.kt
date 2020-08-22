package com.popalay.tracktor.ui.dialog

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.success
import com.popalay.tracktor.ui.widget.Chip
import com.popalay.tracktor.ui.widget.ChipGroup
import com.popalay.tracktor.utils.rememberMutableState
import java.util.UUID

@Composable
fun AddCategoryDialog(
    trackerCategories: List<Category>,
    allCategories: List<Category>,
    onDismissRequest: () -> Unit,
    onSave: (Set<Category>) -> Unit
) {
    var newValue by rememberMutableState { "" }
    var newCategories by rememberMutableState { allCategories.toSet() }
    var selectedCategories by rememberMutableState { trackerCategories.toSet() }
    var isCustomCategoryValid by rememberMutableState { false }
    var isCustomCategoryCreation by rememberMutableState { false }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.add_category_title)) },
        text = {
            ChipGroup {
                newCategories.forEach {
                    Chip(
                        isSelected = it in selectedCategories,
                        onClick = {
                            selectedCategories = if (it in selectedCategories) {
                                selectedCategories - it
                            } else {
                                selectedCategories + it
                            }
                        }
                    ) {
                        Text(it.name)
                    }
                }
                Chip(
                    isSelected = isCustomCategoryCreation,
                    bordered = !isCustomCategoryValid,
                    contentColor = MaterialTheme.colors.onBackground,
                    activeColor = if (isCustomCategoryValid) MaterialTheme.colors.success else MaterialTheme.colors.secondary,
                    onClick = {
                        if (isCustomCategoryValid) {
                            val category = Category(UUID.randomUUID().toString(), newValue)
                            newCategories = newCategories + category
                            selectedCategories = selectedCategories + category
                            isCustomCategoryCreation = false
                            isCustomCategoryValid = false
                            newValue = ""
                        } else {
                            isCustomCategoryCreation = true
                        }
                    }
                ) {
                    Icon(if (isCustomCategoryValid) Icons.Default.Done else Icons.Default.Add)
                }
            }
            if (isCustomCategoryCreation) {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newValue,
                    label = { Text(stringResource(R.string.add_category_new_category_label)) },
                    activeColor = MaterialTheme.colors.onSurface,
                    onValueChange = {
                        newValue = it
                        isCustomCategoryValid = it.isNotBlank()
                    }
                )
            }
        },
        confirmButton = {
            Button(
                enabled = newValue.isNotBlank() || trackerCategories.toSet() != selectedCategories,
                onClick = { onSave(selectedCategories) }
            ) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    )
}