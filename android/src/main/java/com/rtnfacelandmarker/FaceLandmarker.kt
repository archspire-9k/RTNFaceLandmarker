package com.rtnfacelandmarker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.Choreographer
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.ReactApplicationContext
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalGetImage class FaceLandmarker(context: Context) : LinearLayout(context) {

    private val composeView: ComposeView = ComposeView(context)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var defaultDetector: FaceMeshDetector

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).toTypedArray()
    }

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()

        defaultDetector = FaceMeshDetection.getClient(
        )

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setLayoutParams(layoutParams)
        orientation = VERTICAL

        composeView.setContent {
            if (allPermissionsGranted()) {
                Log.d("Started", "Permissions granted")
                CameraView(
                    executor = cameraExecutor,
                    defaultDetector = defaultDetector,
                    context = LocalContext.current
                )
            }
        }
        composeView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(composeView)

        setupLayoutHack()
        manuallyLayoutChildren()
    }

    private fun setupLayoutHack() {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                manuallyLayoutChildren()
                viewTreeObserver.dispatchOnGlobalLayout()
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    private fun manuallyLayoutChildren() {
        try {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.measure(
                    MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
                )
                child.layout(0, 0, child.measuredWidth, child.measuredHeight)
            }
        } catch (e: Exception) {
            Log.d("Started", "$e.message")
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}
