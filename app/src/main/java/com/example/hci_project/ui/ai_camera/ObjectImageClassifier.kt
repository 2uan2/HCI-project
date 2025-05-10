package com.example.hci_project.ui.ai_camera

import TfLiteObjectClassifier
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.hci_project.domain.Classification
import android.graphics.Bitmap
import androidx.core.graphics.toRectF
import com.example.hci_project.domain.ObjectClassifier
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.defaults.PredefinedCategory

class ObjectImageClassifier (
    private val classifier: TfLiteObjectClassifier,
    private val onResult: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

//    private var frameSkipCount = 0

    override fun analyze(image: ImageProxy) {
        if (classifier.detector == null) {
            classifier.setupClassifier()
        }
//        if (frameSkipCount % 60 == 0) {
        val rotationDegrees = image.imageInfo.rotationDegrees
        val bitmap = image.toBitmap()
        .centerCrop(320, 320)

        val image = InputImage.fromBitmap(bitmap, rotationDegrees)
        val objects = mutableListOf<Classification>()
        classifier.detector?.process(image)
            ?.addOnSuccessListener { detectedObjects ->
                detectedObjects.map { detectedObject ->
                    val boundingBox = detectedObject.boundingBox
                    val trackingId = detectedObject.trackingId
                    for (label in detectedObject.labels) {
                        val text = label.text
                        val confidence = label.confidence
                        val classification = Classification(name = text, score = confidence, boundingBox = boundingBox.toRectF())
                        objects.add(classification)
                    }
                }
            }
            ?.addOnFailureListener { e ->
                e.printStackTrace()
            }
        onResult(objects)

    }

}

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if(xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}