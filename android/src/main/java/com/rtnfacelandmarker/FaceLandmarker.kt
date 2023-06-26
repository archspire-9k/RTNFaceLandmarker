package com.rtnfacelandmarker

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.platform.ComposeView


class FaceLandmarker(context: Context) : LinearLayout(context) {

    private var preview: PreviewView
    private var mCameraProvider: ProcessCameraProvider? = null
    private val composeView: ComposeView = ComposeView(context)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var defaultDetector: FaceMeshDetector
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

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
