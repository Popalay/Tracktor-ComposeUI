package com.popalay.tracktor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.state
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.Density
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags

val WindowInsetsAmbient = ambientOf<InnerPadding>()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val view = window.decorView
        view.setEdgeToEdgeSystemUiFlags()

        setContent {
            val insetsState = state { ViewCompat.getRootWindowInsets(view)?.systemWindowInsets ?: Insets.NONE }

            ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
                insetsState.value = insets.systemGestureInsets
                insets
            }
            val padding = with(Density(ContextAmbient.current)) {
                InnerPadding(
                    insetsState.value.left.toDp(),
                    insetsState.value.top.toDp(),
                    insetsState.value.right.toDp(),
                    insetsState.value.bottom.toDp()
                )
            }
            Providers(WindowInsetsAmbient provides padding) {
                App()
            }
        }
    }
}