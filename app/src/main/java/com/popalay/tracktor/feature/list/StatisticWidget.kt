package com.popalay.tracktor.feature.list

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.HorizontalGradient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.annotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.Statistic
import com.popalay.tracktor.feature.list.AnimationState.STATE_END
import com.popalay.tracktor.feature.list.AnimationState.STATE_START
import com.popalay.tracktor.gradients
import com.popalay.tracktor.ui.widget.DoughnutChart
import com.popalay.tracktor.utils.addStyle
import com.popalay.tracktor.utils.toRelativeFormat
import kotlin.math.max
import kotlin.math.min

private enum class AnimationState {
    STATE_START, STATE_END
}

private val opacityKey = FloatPropKey()

private val tweenDefinition = transitionDefinition<AnimationState> {
    state(STATE_START) {
        this[opacityKey] = 0F
    }
    state(STATE_END) {
        this[opacityKey] = 1F
    }
    transition(fromState = STATE_START, toState = STATE_END) {
        opacityKey using tween(durationMillis = 1000)
    }
}

@Composable
fun StatisticWidget(
    statistic: Statistic,
    animate: Boolean,
    modifier: Modifier = Modifier
) {
    val transitionState = transition(
        definition = tweenDefinition,
        initState = if (animate) STATE_START else STATE_END,
        toState = STATE_END
    )
    val opacityValue = transitionState[opacityKey]

    WithConstraints {
        Card(
            elevation = 2.dp,
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.background(HorizontalGradient(gradients[0], 0F, constraints.maxWidth.toFloat()), alpha = 0.5F)
                    .height(150.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DoughnutChart(statistic.overallProgress, animate, modifier = Modifier.fillMaxSize().weight(1F))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(2F).fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                    val lines = listOf(
                        annotatedString {
                            append(stringResource(R.string.progress_statistic_trackers_amount, statistic.trackerCount))
                            addStyle(SpanStyle(fontWeight = FontWeight.Black), statistic.trackerCount.toString())
                        },

                        annotatedString {
                            append(stringResource(R.string.progress_statistic_trackers_with_progress, statistic.trackersWithProgress))
                            addStyle(SpanStyle(fontWeight = FontWeight.Black), statistic.trackersWithProgress.toString())
                        },

                        annotatedString {
                            append(stringResource(R.string.progress_statistic_most_progressive_tracker, statistic.mostProgressiveTracker.title))
                            addStyle(SpanStyle(fontWeight = FontWeight.Black), statistic.mostProgressiveTracker.title)
                        },

                        annotatedString {
                            append(
                                stringResource(
                                    R.string.progress_statistic_last_updated_tracker,
                                    statistic.lastUpdatedTracker.title,
                                    statistic.lastUpdatedTrackerDate.toRelativeFormat()
                                )
                            )
                            addStyle(SpanStyle(fontWeight = FontWeight.Black), statistic.lastUpdatedTracker.title)
                            addStyle(SpanStyle(fontWeight = FontWeight.Black), statistic.lastUpdatedTrackerDate.toRelativeFormat())
                        },
                    )

                    lines.forEachIndexed { index, item ->
                        Text(
                            item,
                            color = contentColor().copy(alpha = min(max(opacityValue * lines.size - index, 0F), 1F)),
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.animateContentSize()
                        )
                    }
                }
            }
        }
    }
}