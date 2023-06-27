package com.rtnfacelandmarker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.RTNFaceLandmarkerManagerDelegate;
import com.facebook.react.viewmanagers.RTNFaceLandmarkerManagerInterface;

@ReactModule(name = FaceLandmarkerManager.NAME)
public class FaceLandmarkerManager extends SimpleViewManager<FaceLandmarker> implements RTNFaceLandmarkerManagerInterface<FaceLandmarker> {

    private final ViewManagerDelegate<FaceLandmarker> mDelegate;

    static final String NAME = "RTNFaceLandmarker";

    public FaceLandmarkerManager(ReactApplicationContext mCallerContext) {
        mDelegate = new RTNFaceLandmarkerManagerDelegate<>(this);
    }

    @Nullable
    @Override
    protected ViewManagerDelegate<FaceLandmarker> getDelegate() {
        return mDelegate;
    }

    @NonNull
    @Override
    public String getName() {
        return FaceLandmarkerManager.NAME;
    }

    @NonNull
    @Override
    protected FaceLandmarker createViewInstance(@NonNull ThemedReactContext context) {
        return new FaceLandmarker(context);
    }
}