package com.rtnfacelandmarker

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.platform.ComposeView

class FaceLandmarker(context: Context) : LinearLayout(context) {

    private val composeView: ComposeView = ComposeView(context)

    init {
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setLayoutParams(layoutParams)
        orientation = VERTICAL
        composeView.setContent {
            FaceLandmarkerText()
        }

        addView(composeView)
    }
}
