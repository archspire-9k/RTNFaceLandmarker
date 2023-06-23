package com.rtnfacelandmarker

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView

class FaceLandmarker(context: Context) : LinearLayout(context) {

    private val textView : TextView = TextView(context)
    private val composeView: ComposeView = ComposeView(context)

    init {
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setLayoutParams(layoutParams)
        orientation = VERTICAL
        textView.text = "Hello from xml"
        textView.textSize = 30f
        textView.setTextColor(Color.BLUE)
        addView(textView)

        composeView.setContent {
            FaceLandmarkerText()
        }

        addView(composeView)
    }
}
