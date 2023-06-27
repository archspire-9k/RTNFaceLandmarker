package com.rtnfacelandmarker

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.platform.ComposeView
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceLandmarker(context: Context) : LinearLayout(context) {

    // private var preview: PreviewView
    // private var mCameraProvider: ProcessCameraProvider? = null
    private val composeView: ComposeView = ComposeView(context)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var defaultDetector: FaceMeshDetector
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    init {

        cameraExecutor = Executors.newSingleThreadExecutor()

        defaultDetector = FaceMeshDetection.getClient(
//            FaceMeshDetectorOptions.Builder()
//                .setUseCase(FaceMeshDetectorOptions.BOUNDING_BOX_ONLY)
//                .build()
        )
        
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setLayoutParams(layoutParams)
        orientation = VERTICAL

        // preview = PreviewView(context)
        // preview.layoutParams = ViewGroup.LayoutParams(
        //     ViewGroup.LayoutParams.MATCH_PARENT,
        //     ViewGroup.LayoutParams.MATCH_PARENT
        // )
        // addView(preview)

        composeView.setContent {
            if (shouldShowCamera.value) {
                CameraView(
                    executor = cameraExecutor,
                    defaultDetector = defaultDetector,
                    context = LocalContext.current
                )
            }
        }

        addView(composeView)
    }

    // private fun setupLayoutHack() {
    //     Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
    //         override fun doFrame(frameTimeNanos: Long) {
    //             manuallyLayoutChildren()
    //             viewTreeObserver.dispatchOnGlobalLayout()
    //             Choreographer.getInstance().postFrameCallback(this)
    //         }
    //     })
    // }

    // private fun manuallyLayoutChildren() {
    //     for (i in 0 until childCount) {
    //         val child = getChildAt(i)
    //         child.measure(
    //             MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
    //             MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
    //         )
    //         child.layout(0, 0, child.measuredWidth, child.measuredHeight)
    //     }
    // }

}
