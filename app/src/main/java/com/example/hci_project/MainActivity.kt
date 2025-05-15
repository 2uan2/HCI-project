package com.example.hci_project

import CallApp
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hci_project.ui.theme.HCI_projectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }
        val app = application as VideoCallingApp
        setContent {
            val systemDarkMode = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemDarkMode) }
            HCI_projectTheme(
                darkTheme = isDarkMode
            ) {
                CallApp(
                    app,
                    isDarkMode = isDarkMode,
                    onDarkThemeClicked = {
                        Log.i("MainActivity", "dark: $isDarkMode")
                        isDarkMode = !isDarkMode
                    }
                )
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
        )
    }
}