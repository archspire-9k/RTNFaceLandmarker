package com.rtnfacelandmarker

import androidx.camera.core.ExperimentalGetImage
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RTNFaceLandmarkerManagerInterface
import com.facebook.react.viewmanagers.RTNFaceLandmarkerManagerDelegate

@ExperimentalGetImage @ReactModule(name = FaceLandmarkerManager.NAME)
class FaceLandmarkerManager(private val mCallerContext: ReactApplicationContext) :
    SimpleViewManager<FaceLandmarker>(), RTNFaceLandmarkerManagerInterface<FaceLandmarker?> {

    private val mDelegate: ViewManagerDelegate<FaceLandmarker>

    init {
        mDelegate = RTNFaceLandmarkerManagerDelegate(this)
    }

    override fun getDelegate(): ViewManagerDelegate<FaceLandmarker> {
        return mDelegate
    }

    override fun getName(): String {
        return NAME
    }

    override fun createViewInstance(reactContext: ThemedReactContext): FaceLandmarker {
        val reactnativeScannerView = FaceLandmarker(reactContext)
        reactnativeScannerView.setUpCamera(reactContext)
        return reactnativeScannerView
    }

    companion object {
        const val NAME = "FaceLandmarker"
    }

}