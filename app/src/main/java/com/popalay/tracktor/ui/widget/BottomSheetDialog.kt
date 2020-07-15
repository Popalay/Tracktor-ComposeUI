package com.popalay.tracktor.ui.widget

import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.compose.ExperimentalComposeApi
import androidx.compose.Recomposer
import androidx.compose.currentComposer
import androidx.compose.onActive
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.ui.core.Modifier
import androidx.ui.core.ViewAmbient
import androidx.ui.core.semantics.semantics
import androidx.ui.core.setContent
import androidx.ui.foundation.Box
import androidx.ui.foundation.semantics.dialog
import androidx.ui.material.MaterialTheme
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun BottomSheetDialog(onCloseRequest: () -> Unit, children: @Composable() () -> Unit) {
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
                Box(backgroundColor = currentColors.background, modifier = Modifier.semantics { this.dialog = true }, children = children)
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

    fun setContent(children: @Composable() () -> Unit) {
        composition = frameLayout.setContent(recomposer, children)
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