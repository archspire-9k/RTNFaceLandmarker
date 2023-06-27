package com.rtnfacelandmarker

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import java.util.Collections

class FaceLandmarkerPackage : ReactPackage {
  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> = 
  Collections.singletonList(FaceLandmarkerManager(reactContext))

  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> =
    emptyList()
}