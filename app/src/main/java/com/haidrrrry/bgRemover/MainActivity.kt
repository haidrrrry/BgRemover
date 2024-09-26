package com.haidrrrry.bgRemover


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haidrrrry.backgroundremover.utils.ImageProcessor
import com.haidrrrry.backgroundremover.utils.ImageProcessorListener


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackgroundRemoverApp(context = this) // Pass the context to the composable function
        }
    }
}

@Composable
fun BackgroundRemoverApp(context: Context) {
    var originalImage by remember { mutableStateOf<Bitmap?>(null) }
    var processedImage by remember { mutableStateOf<Bitmap?>(null) }

    // Initialize the ImageProcessor with a listener to update the processed image
    val imageProcessor = remember {
        ImageProcessor(object : ImageProcessorListener {
            override fun onImageUpdated(image: Bitmap?) {
                processedImage = image
            }
        })
    }

    // Load the original image from drawable resources
    LaunchedEffect(Unit) {
        originalImage = decodeSampledBitmapFromResource(context, R.drawable.testimage, 800, 800)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Show the original image if available
        originalImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Original Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } ?: run {
            Text("No original image to display")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to remove background
        Button(onClick = {
            // Process the image to remove the background
            originalImage?.let { imageProcessor.chooseImage(it, isForeground = true) }
        }) {
            Text("Remove Background")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show the processed image if available
        processedImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Processed Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } ?: run {
            Text("No processed image to display")
        }
    }
}

// Function to decode bitmap while downsampling
fun decodeSampledBitmapFromResource(context: Context, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap? {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeResource(context.resources, resId, options)

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(context.resources, resId, options)
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        // Calculate the largest inSampleSize value that keeps both height and width larger than the requested height and width
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
