package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.LayoutDirection
import androidx.ui.core.Modifier
import androidx.ui.core.Popup
import androidx.ui.core.PopupPositionProvider
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Card
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.unit.IntOffset
import androidx.ui.unit.IntSize
import androidx.ui.unit.dp
import com.popalay.tracktor.gradients
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.ui.widget.ChartWidget

@Composable
fun ChartAppBar(
    tracker: TrackerWithRecords,
    onArrowClicked: () -> Unit = {}
) {
    val selectedValue = state<Pair<Offset, Double>?> { null }

    TopAppBar(modifier = Modifier.preferredHeight(240.dp)) {
        if (selectedValue.value != null) {
            val (offset, value) = selectedValue.value!!

            CartValuePopup(offset, tracker, value)
        }

        Column(modifier = Modifier.padding(bottom = 4.dp)) {
            Row {
                IconButton(onClick = onArrowClicked) {
                    Icon(Icons.Default.ArrowBack)
                }
                Text(
                    tracker.tracker.title,
                    modifier = Modifier.gravity(Alignment.CenterVertically),
                    style = MaterialTheme.typography.subtitle1
                )
            }

            val gradient = gradients.getValue(tracker.tracker.unit)
            ChartWidget(
                modifier = Modifier.preferredHeight(200.dp),
                data = tracker.records.map { it.value },
                gradient = gradient,
                pointColor = Color.White,
                touchable = true,
                onPointSelected = { offset, value ->
                    selectedValue.value = offset to value
                },
                onPointUnSelected = {
                    selectedValue.value = null
                }
            )
        }
    }
}

@Composable
private fun CartValuePopup(offset: Offset, tracker: TrackerWithRecords, value: Double) {
    val positionProvider = remember(offset) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                parentLayoutPosition: IntOffset,
                parentLayoutSize: IntSize,
                layoutDirection: LayoutDirection,
                popupSize: IntSize
            ): IntOffset {
                var popupGlobalPosition = IntOffset(0, 0)
                val intOffset = IntOffset(offset.x.toInt() - popupSize.width / 2, offset.y.toInt())

                val parentAlignmentPoint = Alignment.TopStart.align(
                    IntSize(parentLayoutSize.width, parentLayoutSize.height),
                    layoutDirection
                )
                val relativePopupPos = Alignment.TopStart.align(
                    IntSize(popupSize.width, popupSize.height),
                    layoutDirection
                )

                popupGlobalPosition += IntOffset(parentLayoutPosition.x, parentLayoutPosition.y)
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
        Card(color = Color.White) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.Black,
                text = tracker.format(value)
            )
        }
    }
}