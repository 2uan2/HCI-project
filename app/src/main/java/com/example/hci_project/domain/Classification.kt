package com.example.hci_project.domain

import android.graphics.RectF

data class Classification(
    val name: String,
    val score: Float,
    val boundingBox: RectF? = null
)
