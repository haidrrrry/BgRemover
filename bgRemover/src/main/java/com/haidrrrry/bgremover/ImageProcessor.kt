package com.haidrrrry.backgroundremover.utils

import android.graphics.Bitmap
import android.util.Log

/**
 * ImageProcessor handles the selection and processing of foreground and background images.
 * It provides methods to generate masks and update the UI when images are processed.
 */
class ImageProcessor(private val listener: ImageProcessorListener) {

    private var foregroundImage: Bitmap? = null
    private var maskImage: Bitmap? = null
    private var maskBgImage: Bitmap? = null
    private var bgImage: Bitmap? = null

    private val segmentHelper = SegmentHelper(object : ProcessedListener {
        override fun imageProcessed() {
            updateMaskImage()
        }
    })

    var selectedMode: DisplayMode = DisplayMode.MASK
        set(value) {
            field = value
            notifyImageUpdated() // Notify listener when mode changes
        }

    /**
     * Choose an image for processing.
     * @param bmp The bitmap image to be processed.
     * @param isForeground If true, the image is treated as a foreground image; otherwise, it's a background image.
     */
    fun chooseImage(bmp: Bitmap, isForeground: Boolean) {
        if (bmp.isEmpty()) {
            Log.e("ImageProcessor", "Cannot choose an empty image.")
            return
        }

        if (isForeground) {
            foregroundImage = bmp
            segmentHelper.processImage(foregroundImage!!)
        } else {
            bgImage = bmp
            generateMaskBackgroundImage()
        }
    }

    private fun generateMaskBackgroundImage() {
        if (foregroundImage != null && bgImage != null) {
            maskBgImage = segmentHelper.generateMaskBgImage(foregroundImage!!, bgImage!!)
        } else {
            Log.e("ImageProcessor", "Foreground or background image is null.")
        }
        notifyImageUpdated()
    }

    private fun updateMaskImage() {
        foregroundImage?.let {
            maskImage = segmentHelper.generateMaskImage(it)
            notifyImageUpdated()
        } ?: Log.e("ImageProcessor", "Foreground image is null. Cannot generate mask.")
    }

    private fun notifyImageUpdated() {
        listener.onImageUpdated(getCurrentImage())
    }

    /**
     * Get the currently displayed image based on the selected mode.
     * @return The current image bitmap or null if no valid image is available.
     */
    fun getCurrentImage(): Bitmap? {
        return when (selectedMode) {
            DisplayMode.NORMAL -> {
                Log.d("ImageProcessor", "Displaying normal image")
                foregroundImage
            }
            DisplayMode.MASK -> {
                Log.d("ImageProcessor", "Displaying mask image")
                maskImage
            }
            DisplayMode.CUSTOM_BG -> {
                Log.d("ImageProcessor", "Displaying image with custom background")
                maskBgImage
            }
        }
    }
}

/**
 * Extension function to check if a Bitmap is empty.
 */
private fun Bitmap?.isEmpty(): Boolean {
    return this == null || (this.width == 0 || this.height == 0)
}

/**
 * Listener interface to notify when image processing is completed.
 */
interface ImageProcessorListener {
    fun onImageUpdated(image: Bitmap?)
}

/**
 * Enum class to define the display modes for images.
 */

