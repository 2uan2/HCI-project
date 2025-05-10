import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.example.hci_project.domain.Classification
import com.example.hci_project.domain.ObjectClassifier
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions

class TfLiteObjectClassifier(
    private val context: Context,
    private val threshold: Float = 0.5f,
    private val maxResults: Int = 1,
) {

    var detector: ObjectDetector? = null

    fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()

//        val options = ObjectDetector.ObjectDetectorOptions.builder()
//            .setBaseOptions(baseOptions)
//            .setMaxResults(maxResults)
//            .setScoreThreshold(threshold)
//            .build()
        try {
//            detector = ObjectDetector.createFromFileAndOptions(
//                context,
//                "yolov5.tflite",
//                options
//            )
            detector = ObjectDetection.getClient(options)

        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

//    override fun classify(
//        bitmap: Bitmap,
//        rotation: Int
//    ): List<Classification> {
//        if (detector == null) {
//            setupClassifier()
//        }
//
//        val imageProcessor = ImageProcessor.Builder().build()
//        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
//
//        val imageProcessingOptions = ImageProcessingOptions.builder()
//            .setOrientation(getOrientationFromRotation(rotation))
//            .build()
//
//        val result = detector?.detect(tensorImage, imageProcessingOptions)
//
//        return result?.flatMap { detection ->
//            detection.categories.map { category ->
//                Classification(
//                    name = category.displayName,
//                    score = category.score,
//                    boundingBox = detection.boundingBox,
//                )
//            }
//        }?.distinctBy { it.name } ?: emptyList()
//    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when(rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

}