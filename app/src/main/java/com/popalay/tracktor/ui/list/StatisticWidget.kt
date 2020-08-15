package com.popalay.tracktor.ui.list

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.gradients
import com.popalay.tracktor.model.Statistic
import com.popalay.tracktor.ui.list.AnimationState.STATE_END
import com.popalay.tracktor.ui.list.AnimationState.STATE_START
import com.popalay.tracktor.ui.widget.DoughnutChart
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
                        AnnotatedString.Builder().apply {
                            append("You have ")
                            append(AnnotatedString(statistic.trackerCount.toString(), SpanStyle(fontWeight = FontWeight.Black)))
                            append(" trackers")
                        }.toAnnotatedString(),

                        AnnotatedString.Builder().apply {
                            append("You have ")
                            append(AnnotatedString(statistic.trackersWithProgress.toString(), SpanStyle(fontWeight = FontWeight.Black)))
                            append(" trackers with progress")
                        }.toAnnotatedString(),

                        AnnotatedString.Builder().apply {
                            append(AnnotatedString(statistic.mostProgressiveTracker.title, SpanStyle(fontWeight = FontWeight.Black)))
                            append(" is your most progressive tracker")
                        }.toAnnotatedString(),

                        AnnotatedString.Builder().apply {
                            append(AnnotatedString(statistic.lastUpdatedTracker.title, SpanStyle(fontWeight = FontWeight.Black)))
                            append(" was updated ")
                            append(AnnotatedString(statistic.lastUpdatedTrackerDate.toRelativeFormat(), SpanStyle(fontWeight = FontWeight.Black)))
                        }.toAnnotatedString(),
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