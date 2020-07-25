package com.popalay.tracktor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Providers
import androidx.compose.ambientOf
import androidx.compose.state
import androidx.compose.structuralEqualityPolicy
import androidx.core.view.ViewCompat
import androidx.ui.core.setContent
import androidx.ui.layout.InnerPadding
import androidx.ui.unit.Density
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags

val WindowInsetsAmbient = ambientOf<InnerPadding>(policy = structuralEqualityPolicy())

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContent {
            val paddingState = state { InnerPadding() }

            val view = window.decorView
            view.setEdgeToEdgeSystemUiFlags()
            ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
                paddingState.value = with(Density(view.context)) {
                    InnerPadding(
                        insets.systemWindowInsets.left.toDp(),
                        insets.systemWindowInsets.top.toDp(),
                        insets.systemWindowInsets.right.toDp(),
                        insets.systemWindowInsets.bottom.toDp()
                    )
                }
                insets
            }
            Providers(WindowInsetsAmbient provides paddingState.value) {
                App()
            }
        }
    }
}