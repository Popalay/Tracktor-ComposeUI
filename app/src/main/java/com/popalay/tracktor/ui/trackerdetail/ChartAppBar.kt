package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntBounds
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.gradient
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.ui.widget.ChartWidget
import com.popalay.tracktor.ui.widget.TopAppBar
import com.popalay.tracktor.utils.inject

@Composable
fun ChartAppBar(
    tracker: TrackerWithRecords,
    onArrowClicked: () -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {}
) {
    val selectedValue = state<Pair<Offset, Int>?> { null }

    val insets = WindowInsetsAmbient.current
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onArrowClicked) {
                Icon(Icons.Default.ArrowBack)
            }
        },
        title = { Text(tracker.tracker.title) },
        actions = {
            IconButton(onClick = onUndoClicked) {
                Icon(Icons.Default.Undo)
            }
            IconButton(onClick = onDeleteClicked) {
                Icon(Icons.Default.DeleteForever)
            }
        },
        modifier = Modifier.preferredHeight(insets.top + 240.dp),
        contentModifier = Modifier.padding(bottom = 4.dp, top = insets.top)
    ) {
        val gradient = remember(tracker) { tracker.tracker.compatibleUnit.gradient }
        ChartWidget(
            modifier = Modifier.preferredHeight(200.dp),
            data = tracker.records.map { it.value },
            gradient = gradient,
            pointColor = Color.White,
            touchable = true,
            onPointSelected = { offset, index ->
                selectedValue.value = offset to index
            },
            onPointUnSelected = {
                selectedValue.value = null
            }
        )

        if (selectedValue.value != null) {
            val (offset, index) = requireNotNull(selectedValue.value)

            ChartValuePopup(offset, tracker, tracker.records[index])
        }
    }
}

@Composable
private fun ChartValuePopup(offset: Offset, trackerWithRecords: TrackerWithRecords, record: ValueRecord) {
    val positionProvider = remember(offset) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                parentGlobalBounds: IntBounds,
                windowGlobalBounds: IntBounds,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                var popupGlobalPosition = IntOffset(0, 0)
                val intOffset = IntOffset(offset.x.toInt() - popupContentSize.width / 2, offset.y.toInt())

                val parentAlignmentPoint = Alignment.TopStart.align(parentGlobalBounds.toSize(), layoutDirection)
                val relativePopupPos = Alignment.TopStart.align(popupContentSize, layoutDirection)

                popupGlobalPosition += IntOffset(parentGlobalBounds.left, parentGlobalBounds.top)
                popupGlobalPosition += parentAlignmentPoint
                popupGlobalPosition -= IntOffset(relativePopupPos.x, relativePopupPos.y)

                val resolvedOffset = IntOffset(
                    intOffset.x * (if (layoutDirection == LayoutDirection.Ltr) 1 else -1),
                    intOffset.y
                )
                popupGlobalPosition += resolvedOffset

                return popupGlobalPosition
            }
        }
    }

    Popup(positionProvider) {
        val formatter: ValueRecordFormatter by inject()

        Card(color = Color.White) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.Black,
                text = formatter.format(trackerWithRecords.tracker, record)
            )
        }
    }
}