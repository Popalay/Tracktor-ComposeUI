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
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Card
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.unit.IntBounds
import androidx.ui.unit.IntOffset
import androidx.ui.unit.IntSize
import androidx.ui.unit.dp
import androidx.ui.unit.toSize
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.gradients
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.ui.widget.ChartWidget
import com.popalay.tracktor.ui.widget.TopAppBar
import com.popalay.tracktor.utils.inject

@Composable
fun ChartAppBar(
    tracker: TrackerWithRecords,
    onArrowClicked: () -> Unit = {}
) {
    val selectedValue = state<Pair<Offset, Int>?> { null }

    TopAppBar(
        title = {
            Text(
                tracker.tracker.title,
                modifier = Modifier.gravity(Alignment.CenterVertically),
                style = MaterialTheme.typography.subtitle1
            )
        },
        navigationIcon = {
            IconButton(onClick = onArrowClicked) {
                Icon(Icons.Default.ArrowBack)
            }
        },
        modifier = Modifier.preferredHeight(240.dp),
        contentModifier = Modifier.padding(bottom = 4.dp, top = WindowInsetsAmbient.current.top)
    ) {
        val gradient = gradients.getValue(tracker.tracker.unit)
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
                parentLayoutBounds: IntBounds,
                layoutDirection: LayoutDirection,
                popupSize: IntSize
            ): IntOffset {
                var popupGlobalPosition = IntOffset(0, 0)
                val intOffset = IntOffset(offset.x.toInt() - popupSize.width / 2, offset.y.toInt())

                val parentAlignmentPoint = Alignment.TopStart.align(parentLayoutBounds.toSize(), layoutDirection)
                val relativePopupPos = Alignment.TopStart.align(popupSize, layoutDirection)

                popupGlobalPosition += IntOffset(parentLayoutBounds.left, parentLayoutBounds.top)
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