package com.popalay.tracktor.ui.widget

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.ui.dialog.AddCategoryDialog
import com.popalay.tracktor.ui.widget.TrackerCategoryAnimationState.STATE_END
import com.popalay.tracktor.ui.widget.TrackerCategoryAnimationState.STATE_START

private enum class TrackerCategoryAnimationState {
    STATE_START, STATE_END
}

private val offsetKey = FloatPropKey()

private val tweenDefinition = transitionDefinition<TrackerCategoryAnimationState> {
    state(STATE_START) {
        this[offsetKey] = 1F
    }
    state(STATE_END) {
        this[offsetKey] = 0F
    }
    transition(STATE_START, STATE_END) {
        offsetKey using tween(
            easing = LinearOutSlowInEasing,
            delayMillis = AnimationConstants.DefaultDurationMillis
        )
    }
}

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
    val transitionState = transition(
        definition = tweenDefinition,
        toState = STATE_END,
        initState = STATE_START
    )

    if (isAddCategoryDialogShowing) {
        AddCategoryDialog(
            categories,
            availableCategories.minus(Category.All),
            onCloseRequest = onDialogDismissed,
            onSave = { onSave(it.toList()) }
        )
    }

    WithConstraints {
        ScrollableRow(modifier.fillMaxWidth()) {
            Spacer(Modifier.width(16.dp))
            Chip(
                onClick = { onAddCategoryClicked() },
                activeColor = MaterialTheme.colors.surface,
                contentColor = contentColorFor(MaterialTheme.colors.surface),
                modifier = Modifier.offset(x = maxWidth * transitionState[offsetKey])
            ) {
                Icon(Icons.Default.Add)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.create_tracker_add_category))
            }
            categories.forEachIndexed { index, item ->
                Spacer(Modifier.width(8.dp))
                val offsetValue = transitionState[offsetKey].let { ((it + (index + 1) * it * 2) * maxWidth.value).dp }
                Chip(isSelected = true, modifier = Modifier.offset(x = offsetValue)) {
                    Text(text = item.name)
                }
            }
            Spacer(Modifier.width(16.dp))
        }
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
        Spacer(Modifier.width(8.dp))
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
        Spacer(Modifier.width(16.dp))
    }
}