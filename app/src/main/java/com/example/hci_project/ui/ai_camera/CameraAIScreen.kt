package com.example.hci_project.ui.ai_camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import com.example.hci_project.domain.Classification
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetector
import androidx.core.graphics.scale
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

@Composable
fun CameraAIScreen(
    controller: LifecycleCameraController,
    onObjectReceived: (List<Classification>) -> Unit,
    onClick: () -> Unit,
//    objects: List<Classification>,
//    onPhotoTaken: (Bitmap) -> Unit,
) {
    val context = LocalContext.current
    var imageTaken by remember { mutableStateOf(false) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val localModel = LocalModel.Builder()
        .setAssetFilePath("mobile-object-labeler.tflite")
        // or .setAbsoluteFilePath(absolute file path to model file)
        // or .setUri(URI to model file)
        .build()
    val customObjectDetectorOptions =
        CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()
    val detector = ObjectDetection.getClient(customObjectDetectorOptions)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (imageTaken) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = "photo",
            )
        } else {
            CameraPreview(
                controller = controller,
                modifier = Modifier.fillMaxSize(),
            )
            Button(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                shape = RoundedCornerShape(24),
                modifier = Modifier
//                    .clip(RoundedCornerShape(50))
                    .offset(16.dp, 16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "switch camera",


                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter),
//                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    onClick = {
                        onClick()
                        takePhoto(
                            controller = controller,
                            onPhotoTaken = { bitmap ->
//                                val resizedBitmap = bitmap.scale(1280, 1280)

                                val image = InputImage.fromBitmap(bitmap, 0)
                                val objects = mutableListOf<Classification>()
                                detector.process(image)
                                    .addOnSuccessListener { detectedObjects ->
                                        Log.i("CameraAIScreen", "Detected Objects Count: ${detectedObjects.size}") // Log the number of detected objects
                                        for (detectedObject in detectedObjects) {
                                            Log.i("CameraAIScreen", "Object: BoundingBox=${detectedObject.boundingBox}, Labels=${detectedObject.labels}") // Log the full object
                                            val boundingBox = detectedObject.boundingBox
                                            val trackingId = detectedObject.trackingId
                                            var hasValidLabel = false;
                                            for (label in detectedObject.labels) {
                                                val text = label.text
                                                val confidence = label.confidence
                                                Log.i("CameraAIScreen", "Label: text=$text, confidence=$confidence") // Log the label info
                                                if (confidence > 0.3) { // Use a threshold here
                                                    val classification = Classification(name = text, score = confidence, boundingBox = boundingBox.toRectF())
                                                    objects.add(classification)
                                                    hasValidLabel = true;
                                                }
                                            }
                                            if(!hasValidLabel){
                                                val classification = Classification(name = "Unknown", score = 0.0f, boundingBox = boundingBox.toRectF()) // Add box even with no label
                                                objects.add(classification)
                                            }
                                        }
                                        imageBitmap = bitmap.drawBoundingBoxes(objects).asImageBitmap()//.scale(1280, 1280).asImageBitmap()
                                        imageTaken = true
                                        onObjectReceived(objects.toList())
                                        Log.i("CameraAIScreen", "Objects after filtering: $objects")
                                    }
                                imageBitmap = bitmap.drawBoundingBoxes(objects).asImageBitmap()//.scale(1280, 1280).asImageBitmap()
                                imageTaken = true
                                Log.i("CameraAIScreen", objects.toString())
                            },
                            context = context,
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8))
//                        .padding(32.dp)

//                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(8.dp)
                    )
//                    Icon(
//                        imageVector = Icons.Default.PhotoCamera,
//                        contentDescription = "take photo"
//                    )
                }
            }
        }
    }
}

fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context,
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraAIScreen", "Couldn't take photo: ", exception)
            }
        }
    )
}

fun Bitmap.drawBoundingBoxes(
    detections: List<Classification>,
    boxColor: Int = Color.RED,
    strokeWidth: Float = 2f
): Bitmap {
    // 1. Create a mutable copy of the Bitmap
    val mutableBitmap = this.copy(Bitmap.Config.ARGB_8888, true)

    // 2. Create a Canvas associated with the mutable Bitmap
    val canvas = Canvas(mutableBitmap)

    // 3. Create a Paint object for the bounding boxes
    val paint = Paint().apply {
        color = boxColor
        style = Paint.Style.STROKE // Outline only
        this.strokeWidth = strokeWidth
    }

    // 4. Iterate through the detections
    for (detection in detections) {
        detection.boundingBox?.let { rectF ->
            // 5. Draw the rectangle
            canvas.drawRect(rectF, paint)

            // Optional: Draw the label and confidence
            val labelPaint = Paint().apply {
                color = boxColor
                textSize = 64f
                style = Paint.Style.FILL
            }
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 64f
                style = Paint.Style.FILL
            }
            val labelText = "${detection.name}: ${String.format("%.2f", detection.score)}"
            val textWidth = labelPaint.measureText(labelText)
            val textHeight = labelPaint.textSize

            // Draw a background for the text
            canvas.drawRect(rectF.left, rectF.top - textHeight - 4, rectF.left + textWidth + 4, rectF.top, labelPaint)

            // Draw the text
            canvas.drawText(labelText, rectF.left + 2, rectF.top - 2, textPaint)
        }
    }

    return mutableBitmap
}
