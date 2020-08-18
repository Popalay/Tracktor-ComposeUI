package com.popalay.tracktor.utils

import android.graphics.Matrix
import android.graphics.PathMeasure
import android.graphics.RectF
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.Direction
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import org.koin.core.context.KoinContextHandler
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier


@Composable
inline fun <reified T> inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = remember {
    val context = KoinContextHandler.get()
    context.inject(qualifier, parameters)
}

@Composable
inline fun onBackPressed(crossinline action: () -> Unit) {
    val activity = ContextAmbient.current as AppCompatActivity
    onActive {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                action()
            }
        }
        activity.onBackPressedDispatcher.addCallback(callback)
        onDispose { callback.isEnabled = false }
    }
}

fun Path.getSubPath(start: Float, end: Float): Path {
    val subPath = android.graphics.Path()
    val pathMeasure = PathMeasure(asAndroidPath(), false)
    pathMeasure.getSegment(start * pathMeasure.length, end * pathMeasure.length, subPath, true)
    return subPath.asComposePath()
}

fun Path.transform(
    angle: Float,
    scale: Float
): Path {
    if (angle == 0F && scale == 1F) return this
    val matrix = Matrix()
    val bounds = RectF()
    return asAndroidPath().apply {
        computeBounds(bounds, true)
        matrix.postRotate(angle, bounds.centerX(), bounds.centerY())
        matrix.postScale(scale, scale, bounds.centerX(), bounds.centerY())
        transform(matrix)
    }.asComposePath()
}

fun Modifier.dragGestureFilter(
    onStart: (downPosition: Offset) -> Unit = {},
    onDrag: (dragDistance: Offset) -> Offset = { Offset.Zero },
    onStop: (velocity: Offset) -> Unit = {},
    onCancel: () -> Unit = {},
    canDrag: ((Direction) -> Boolean)? = null,
    startDragImmediately: Boolean = false
): Modifier = composed {
    val dragObserver = remember {
        object : DragObserver {
            override fun onStart(downPosition: Offset) {
                onStart(downPosition)
            }

            override fun onDrag(dragDistance: Offset): Offset {
                return onDrag(dragDistance)
            }

            override fun onStop(velocity: Offset) {
                onStop(velocity)
            }

            override fun onCancel() {
                onCancel()
            }
        }
    }
    dragGestureFilter(dragObserver, canDrag, startDragImmediately)
}

fun AnnotatedString.Builder.addStyle(style: SpanStyle, source: String) {
    val startIndex = toString().indexOf(source)
    check(startIndex != -1) { "${toString()} doesn't contain $source" }
    addStyle(
        style,
        startIndex,
        startIndex + source.length
    )
}