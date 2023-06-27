package com.rtnfacelandmarker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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

    // private var preview: PreviewView
    // private var mCameraProvider: ProcessCameraProvider? = null
    private val composeView: ComposeView = ComposeView(context)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var defaultDetector: FaceMeshDetector

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).toTypedArray()
    }

    init {
        
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
            if (allPermissionsGranted()) {
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

    fun setUpCamera(reactApplicationContext: ReactApplicationContext) {
        cameraExecutor = Executors.newSingleThreadExecutor()

        defaultDetector = FaceMeshDetection.getClient(
        )

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}
