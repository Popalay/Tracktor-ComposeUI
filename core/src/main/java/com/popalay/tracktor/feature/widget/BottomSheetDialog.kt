package com.popalay.tracktor.feature.widget

import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.compose.foundation.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.onActive
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun BottomSheetDialog(onCloseRequest: () -> Unit, children: @Composable () -> Unit) {
    val view = ViewAmbient.current

    @OptIn(ExperimentalComposeApi::class)
    val recomposer = currentComposer.recomposer
    // The recomposer can't change.
    val dialog = remember(view) { BottomSheetDialogWrapper(view, recomposer) }
    dialog.onCloseRequest = onCloseRequest

    onActive {
        dialog.show()

        onDispose {
            dialog.dismiss()
            dialog.disposeComposition()
        }
    }

    val currentColors = MaterialTheme.colors
    val currentTypography = MaterialTheme.typography
    onCommit {
        dialog.setContent {
            MaterialTheme(colors = currentColors, typography = currentTypography) {
                Box(backgroundColor = currentColors.background, modifier = Modifier.semantics { this.dialog() }, children = children)
            }
        }
    }
}

private class BottomSheetDialogWrapper(
    composeView: View,
    private val recomposer: Recomposer
) : BottomSheetDialog(composeView.context) {
    lateinit var onCloseRequest: () -> Unit

    private val frameLayout = CoordinatorLayout(context)
    private var composition: Composition? = null

    init {
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(frameLayout)
        ViewTreeLifecycleOwner.set(frameLayout, ViewTreeLifecycleOwner.get(composeView))
        ViewTreeViewModelStoreOwner.set(frameLayout, ViewTreeViewModelStoreOwner.get(composeView))
    }

    fun setContent(children: @Composable () -> Unit) {
        composition = frameLayout.setContent(recomposer, parentComposition = null, children)
    }

    fun disposeComposition() {
        composition?.dispose()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result = super.onTouchEvent(event)
        if (result) {
            onCloseRequest()
        }

        return result
    }

    override fun cancel() {
        // Prevents the dialog from dismissing itself
        onCloseRequest()
        return
    }

    override fun onBackPressed() {
        onCloseRequest()
    }
}