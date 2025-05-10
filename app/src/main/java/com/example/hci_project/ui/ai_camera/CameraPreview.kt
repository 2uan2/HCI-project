package com.example.hci_project.ui.ai_camera

import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                Log.i("CameraPreview", "Launched")
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}