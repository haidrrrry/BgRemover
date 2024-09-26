package com.haidrrrry.bgremover.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import kotlin.math.roundToInt

/**
 * Merges two bitmaps into one.
 *
 * @param bmp1 The first bitmap to merge.
 * @param bmp2 The second bitmap to merge.
 * @return A new bitmap that is the result of merging bmp1 and bmp2.
 */
fun mergeBitmaps(bmp1: Bitmap, bmp2: Bitmap): Bitmap {
    val merged = Bitmap.createBitmap(bmp1.width, bmp1.height, bmp1.config)
    val canvas = Canvas(merged)
    canvas.drawBitmap(bmp1, Matrix(), null)
    canvas.drawBitmap(bmp2, Matrix(), null)
    return merged
}

/**
 * Resizes a bitmap to the specified width and height.
 *
 * @param bmp The bitmap to resize.
 * @param width The desired width.
 * @param height The desired height.
 * @return A new bitmap resized to the specified dimensions.
 */
fun resizeBitmap(bmp: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(bmp, width, height, true)
}

/**
 * Resizes a bitmap while maintaining its aspect ratio.
 *
 * @param bmp The bitmap to resize.
 * @param width The desired width.
 * @return A new bitmap resized to the specified width, preserving aspect ratio.
 */
fun resizeBitmapWithAspect(bmp: Bitmap, width: Int): Bitmap {
    val aspectRatio: Float = bmp.width / bmp.height.toFloat()
    val height = (width / aspectRatio).roundToInt()
    return resizeBitmap(bmp, width, height)
}

/**
 * Retrieves a bitmap from the specified URI and resizes it to the desired width while maintaining aspect ratio.
 *
 * @param contentResolver The content resolver to access the image data.
 * @param imageUri The URI of the image.
 * @param width The desired width for the resized bitmap.
 * @return A new bitmap retrieved from the URI and resized, or null if retrieval fails.
 */
fun getBitmapFromUri(contentResolver: ContentResolver, imageUri: Uri, width: Int): Bitmap? {
    return try {
        val bmp = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        resizeBitmapWithAspect(bmp, width)
    } catch (e: Exception) {
        // Log the error and return null
        e.printStackTrace() // You can use your preferred logging method here
        null
    }
}


