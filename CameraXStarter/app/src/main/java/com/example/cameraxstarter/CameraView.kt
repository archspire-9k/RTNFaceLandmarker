@file:Suppress("BlockingMethodInNonBlockingContext")

package com.example.cameraxstarter

import android.content.Context
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.Triangle
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import com.google.mlkit.vision.facemesh.FaceMeshPoint
import java.lang.Float.max
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
    Only works in portrait mode so far since the readjustment formula takes the value of width directly
 */
@ExperimentalGetImage
@Composable
fun CameraView(executor: ExecutorService, defaultDetector: FaceMeshDetector, context: Context) {
    var boundsList by remember { mutableStateOf(listOf<FaceMesh>()) }
    val screenHeightPx = remember { mutableStateOf(0f) }
    val screenWidthPx = remember { mutableStateOf(0f) }
    var scaleFactor = 1f
    var scaleHeight: Float
    var scaleWidth: Float
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current

    val builder = Preview.Builder()
    val preview = builder
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .build()
    val previewView = remember { PreviewView(context) }
    val imageAnalysis: ImageAnalysis = remember {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build().also {
                it.setAnalyzer(
                    executor
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
    }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalysis
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    // 3
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                screenHeightPx.value = coordinates.size.height.toFloat()
                screenWidthPx.value = coordinates.size.width.toFloat()
                scaleHeight = screenHeightPx.value / 640
                scaleWidth = screenWidthPx.value / 480
                scaleFactor = max(scaleWidth, scaleHeight)
                Log.d("RATIO", "Composable ratio $scaleHeight : $scaleWidth")
            }
    ) {

        AndroidView(
            { previewView }, modifier = Modifier
                .fillMaxSize()
        )
        Canvas(
            Modifier.fillMaxSize()
        ) {
            for (boundPoints in boundsList) {
                val detectedRegion = boundPoints.boundingBox
                val detectedPointSet = boundPoints.allPoints
                // Gets triangle info
                val triangles: List<Triangle<FaceMeshPoint>> = boundPoints.allTriangles
                Log.d("TRIANGLE", "${detectedPointSet.size}")
                val faceMeshpoints = detectedPointSet.map { pair ->
                    Offset(
                        pair.position.x * scaleFactor - (480 * scaleFactor - screenWidthPx.value) / 2,
                        pair.position.y * scaleFactor
                    )
                }

                for(triangle in triangles) {
                    val trianglePoints = triangle.allPoints.map { pair ->
                        Offset(
                            pair.position.x * scaleFactor - (480 * scaleFactor - screenWidthPx.value) / 2,
                            pair.position.y * scaleFactor
                        )
                    }
                    val point1 = trianglePoints[0]
                    val point2 = trianglePoints[1]
                    val point3 = trianglePoints[2]
                    drawLine(
                        color = Color.Cyan,
                        start = Offset(point1.x, point1.y),
                        end = Offset(point2.x, point2.y),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.Cyan,
                        start = Offset(point2.x, point2.y),
                        end = Offset(point3.x, point3.y),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.Cyan,
                        start = Offset(point1.x, point1.y),
                        end = Offset(point3.x, point3.y),
                        strokeWidth = 4f
                    )
                }

                drawRect(
                    color = Color.Red, topLeft = Offset(
                        detectedRegion.left * scaleFactor - (480 * scaleFactor - screenWidthPx.value) / 2,
                        detectedRegion.top * scaleFactor
                    ), size = Size(
                        detectedRegion.width().toFloat() * scaleFactor,
                        detectedRegion.height().toFloat() * scaleFactor
                    ), style = Stroke(width = 2f)
                )

                drawPoints(
                    points = faceMeshpoints,
                    pointMode = PointMode.Points,
                    color = Color.White,
                    strokeWidth = 4f
                )

            }

        }
    }

}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }