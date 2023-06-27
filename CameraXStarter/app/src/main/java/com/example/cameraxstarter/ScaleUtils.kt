package com.example.cameraxstarter


object ScaleUtils {
//    private fun updateTransformationIfNeeded(imageWidth: Int, imageHeight: Int) {
//        if ( imageWidth <= 0 || imageHeight <= 0) {
//            return
//        }
//        val viewAspectRatio = getWidth().toFloat() / getHeight()
//        val imageAspectRatio: Float = imageWidth as Float / imageHeight
//
//        if (viewAspectRatio > imageAspectRatio) {
//            // The image needs to be vertically cropped to be displayed in this view.
//            scaleFactor = getWidth().toFloat() / imageWidth
//            postScaleHeightOffset = (getWidth().toFloat() / imageAspectRatio - getHeight()) / 2
//        } else {
//            // The image needs to be horizontally cropped to be displayed in this view.
//            scaleFactor = getHeight().toFloat() / imageHeight
//            postScaleWidthOffset = (getHeight().toFloat() * imageAspectRatio - getWidth()) / 2
//        }
//        transformationMatrix.reset()
//        transformationMatrix.setScale(scaleFactor, scaleFactor)
//        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset)
//        if (isImageFlipped) {
//            transformationMatrix.postScale(-1f, 1f, getWidth() / 2f, getHeight() / 2f)
//        }
//        needUpdateTransformation = false
//    }
}