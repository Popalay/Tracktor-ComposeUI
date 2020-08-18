package com.popalay.tracktor.ui.widget

import android.graphics.CornerPathEffect
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.ui.widget.AnimationState.STATE_END
import com.popalay.tracktor.ui.widget.AnimationState.STATE_START
import com.popalay.tracktor.utils.createPolygon
import com.popalay.tracktor.utils.transform
import kotlin.math.roundToInt

private enum class AnimationState {
    STATE_START, STATE_END
}

private val opacityKey = FloatPropKey()
private val sizeKey = FloatPropKey()
private val angleKey = FloatPropKey()

private val startAnimationDefinition = transitionDefinition<AnimationState> {
    state(STATE_START) {
        this[opacityKey] = 0F
        this[sizeKey] = 0F
        this[angleKey] = 0F
    }
    state(STATE_END) {
        this[opacityKey] = 1F
        this[sizeKey] = 1F
        this[angleKey] = 1F
    }
    transition(fromState = STATE_START, toState = STATE_END) {
        opacityKey using tween(durationMillis = 1500)
        sizeKey using spring(
            dampingRatio = 0.7F,
            stiffness = 50F
        )
        angleKey using spring(
            dampingRatio = 0.7F,
            stiffness = 40F
        )
    }
}

private val tickAnimationDefinition = transitionDefinition<AnimationState> {
    state(STATE_START) {
        this[sizeKey] = 1F
    }
    state(STATE_END) {
        this[sizeKey] = 1.02F
    }
    transition(fromState = STATE_START, toState = STATE_END) {
        sizeKey using repeatable(
            AnimationConstants.Infinite,
            tween(
                durationMillis = 500,
                delayMillis = 1500
            ),
            RepeatMode.Reverse
        )
    }
}

@Composable
fun DoughnutChart(progress: Double, animate: Boolean = true, modifier: Modifier = Modifier.fillMaxSize()) {
    val startAnimation = transition(
        definition = startAnimationDefinition,
        initState = if (animate) STATE_START else STATE_END,
        toState = STATE_END
    )
    val tickAnimation = transition(
        definition = tickAnimationDefinition,
        initState = STATE_START,
        toState = STATE_END
    )

    val sizeValue = startAnimation[sizeKey].takeIf { it != 1F } ?: tickAnimation[sizeKey]
    val opacityValue = startAnimation[opacityKey]
    val angleValue = startAnimation[angleKey]

    Stack(modifier) {
        val pathColor = contentColor()

        Canvas(Modifier.fillMaxSize()) {
            val path = createPolygon(size, 6)

            repeat(3) {
                val angle = it * 45F * angleValue

                drawPath(
                    path.transform(angle, sizeValue),
                    pathColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = CornerPathEffect(4.dp.toPx())
                    )
                )
            }
        }

        Text(
            text = "↑${(progress * 100).roundToInt()}%",
            color = contentColor().copy(alpha = opacityValue),
            style = MaterialTheme.typography.h5,
            modifier = Modifier.gravity(Alignment.Center)
        )
    }
}