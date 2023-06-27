package com.example.cameraxstarter

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalGetImage class MainActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var defaultDetector: FaceMeshDetector
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (shouldShowCamera.value) {
                CameraView(
                    executor = cameraExecutor,
                    defaultDetector = defaultDetector,
                    context = LocalContext.current
                )
            }
        }

        requestCameraPermission()

        cameraExecutor = Executors.newSingleThreadExecutor()

        defaultDetector = FaceMeshDetection.getClient(
//            FaceMeshDetectorOptions.Builder()
//                .setUseCase(FaceMeshDetectorOptions.BOUNDING_BOX_ONLY)
//                .build()
        )
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onPause() {
        super.onPause()
        cameraExecutor.shutdown()
    }
}