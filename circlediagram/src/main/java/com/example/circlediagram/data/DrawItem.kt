package com.example.circlediagram.data

import androidx.compose.ui.graphics.Color

data class DrawItem(
    var radius: Float = 300f,
    val startAngle: Float,
    val sweepAngle: Float,
    val color: Color
)
