package com.popalay.tracktor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Providers
import androidx.compose.ambientOf
import androidx.compose.state
import androidx.compose.structuralEqualityPolicy
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.ui.core.ContextAmbient
import androidx.ui.core.setContent
import androidx.ui.layout.InnerPadding
import androidx.ui.unit.Density
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags

val WindowInsetsAmbient = ambientOf<InnerPadding>(policy = structuralEqualityPolicy())

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