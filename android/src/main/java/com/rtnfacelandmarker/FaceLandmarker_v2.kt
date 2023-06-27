package com.rtnfacelandmarker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.Choreographer
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.ReactApplicationContext
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceLandmarker(context: Context) :  LinearLayout(context) {

    //main views
    private val composeView: ComposeView = ComposeView(context)
    private var preview: PreviewView


    private var mCameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var defaultDetector: FaceMeshDetector
    private var analysisUseCase: ImageAnalysis = ImageAnalysis.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_YUV_420_888)
        .build()
    private var boundsList = mutableListOf<FaceMesh>()
    private val screenHeightPx = mutableStateOf(0f)
    private val screenWidthPx = mutableStateOf(0f)
    private var scaleFactor = 1f
    private var scaleHeight: Float = 0f
    private var scaleWidth: Float = 0f

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).toTypedArray()
    }

    init {
        val linearLayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams = linearLayoutParams
        orientation = VERTICAL

        preview = PreviewView(context)
        preview.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(preview)

        composeView.setContent {
            CameraView()
        }

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
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
            )
            child.layout(0, 0, child.measuredWidth, child.measuredHeight)
        }
    }

    fun setUpCamera(reactApplicationContext: ReactApplicationContext) {
        if (allPermissionsGranted()) {
            startCamera(reactApplicationContext)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        defaultDetector = FaceMeshDetection.getClient()

        analysisUseCase.setAnalyzer(
            // newSingleThreadExecutor() will let us perform analysis on a single worker thread
            Executors.newSingleThreadExecutor()
        ) { imageProxy ->
            /**
             * TODO: Separate this snippet into a different file
             * detectFace(imageProxy)
             *}
             */
            val image = BitmapUtils.getBitmap(imageProxy, false)
            if (image != null) {
                defaultDetector.process(InputImage.fromBitmap(image, 0))
                    .addOnSuccessListener { result ->
                        // Task completed successfully
                        if (result != null) {
                          Log.d("SUCCESS","Return $result")
                            boundsList = result
                        }
                        imageProxy.close()
                    }.addOnFailureListener { e ->
                        // Task failed with an exception
                        Log.d("FAIL", "The result has , $e")
                    }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera(reactApplicationContext: ReactApplicationContext) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            mCameraProvider = cameraProvider
            // Preview
            val surfacePreview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    (reactApplicationContext.currentActivity as AppCompatActivity),
                    cameraSelector,
                    surfacePreview,
                    analysisUseCase
                )

            } catch (exc: Exception) {
                Log.d("FAIL", "cam prod error ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(context))
    }
}