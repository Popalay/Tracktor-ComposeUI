package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntBounds
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.data.model.ValueRecord
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.gradient
import com.popalay.tracktor.ui.widget.ChartWidget
import com.popalay.tracktor.utils.inject
import com.popalay.tracktor.utils.rememberMutableState

@Composable
fun ChartCard(tracker: TrackerWithRecords, modifier: Modifier = Modifier) {
    val selectedValue = rememberMutableState<Pair<Offset, Int>?> { null }
    val gradient = remember(tracker) { tracker.tracker.compatibleUnit.gradient }

    Stack(modifier) {
        Card {
            ChartWidget(
                modifier = Modifier.preferredHeight(200.dp),
                data = tracker.records.map { it.value },
                gradient = gradient,
                pointColor = contentColor(),
                touchable = true,
                onPointSelected = { offset, index -> selectedValue.value = offset to index % tracker.records.size },
                onPointUnSelected = {
                    selectedValue.value = null
                }
            )
        }

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
                    intOffset.x,
                    (intOffset.y - popupContentSize.height * 1.5).toInt()
                )
                popupGlobalPosition += resolvedOffset

                return popupGlobalPosition
            }
        }
    }

    Popup(positionProvider) {
        val formatter: ValueRecordFormatter by inject()

        Card(
            backgroundColor = MaterialTheme.colors.onSurface,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colors.surface,
                text = formatter.format(trackerWithRecords.tracker, record)
            )
        }
    }
}