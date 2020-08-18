package com.popalay.tracktor.ui.dialog

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.state
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.success
import com.popalay.tracktor.ui.widget.Chip
import com.popalay.tracktor.ui.widget.ChipGroup
import java.util.UUID

@Composable
fun AddCategoryDialog(
    trackerCategories: List<Category>,
    allCategories: List<Category>,
    onCloseRequest: () -> Unit,
    onSave: (Set<Category>) -> Unit
) {
    val newValue = state { "" }
    val newCategories = state { allCategories.toSet() }
    val selectedCategories = state { trackerCategories.toSet() }
    val isCustomCategoryValid = state { false }
    val isCustomCategoryCreation = state { false }

    AlertDialog(
        onCloseRequest = onCloseRequest,
        title = {
            Column {
                Text(stringResource(R.string.add_category_title))
                Text(stringResource(R.string.common_sorry_for_crash), style = MaterialTheme.typography.caption)
            }
        },
        text = {
            ChipGroup {
                newCategories.value.forEach {
                    Chip(
                        isSelected = it in selectedCategories.value,
                        onClick = {
                            if (it in selectedCategories.value) {
                                selectedCategories.value = selectedCategories.value - it
                            } else {
                                selectedCategories.value = selectedCategories.value + it
                            }
                        }
                    ) {
                        Text(it.name)
                    }
                }
                Chip(
                    isSelected = isCustomCategoryCreation.value,
                    bordered = !isCustomCategoryValid.value,
                    contentColor = MaterialTheme.colors.onBackground,
                    activeColor = if (isCustomCategoryValid.value) MaterialTheme.colors.success else MaterialTheme.colors.secondary,
                    onClick = {
                        if (isCustomCategoryValid.value) {
                            val category = Category(UUID.randomUUID().toString(), newValue.value)
                            newCategories.value = newCategories.value + category
                            selectedCategories.value = selectedCategories.value + category
                            isCustomCategoryCreation.value = false
                            isCustomCategoryValid.value = false
                            newValue.value = ""
                        } else {
                            isCustomCategoryCreation.value = true
                        }
                    }
                ) {
                    Icon(if (isCustomCategoryValid.value) Icons.Default.Done else Icons.Default.Add)
                }
            }
            if (isCustomCategoryCreation.value) {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newValue.value,
                    label = { Text(stringResource(R.string.add_category_new_category_label)) },
                    activeColor = MaterialTheme.colors.onSurface,
                    onValueChange = {
                        newValue.value = it
                        isCustomCategoryValid.value = it.isNotBlank()
                    }
                )
            }
        },
        confirmButton = {
            Button(
                enabled = newValue.value.isNotBlank() || trackerCategories.toSet() != selectedCategories.value,
                onClick = { onSave(selectedCategories.value) }
            ) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = {
            Button(onClick = onCloseRequest) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    )
}