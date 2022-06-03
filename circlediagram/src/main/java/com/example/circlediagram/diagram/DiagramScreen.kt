package com.example.circlediagram.diagram

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circlediagram.R
import com.example.circlediagram.data.Data
import com.example.circlediagram.data.DrawItem
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

@Composable
fun DiagramScreen(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.padding(50.dp)) {
        DrawDiagram(expansesList = Data.getData())
        Text(text = "150 BYN", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}

@Composable
private fun DrawDiagram(expansesList: List<Int>) {
    //for diagram get only 8 values!!!
    val values = if (expansesList.size <= 8) {
        expansesList.sortedDescending()
    } else {
        expansesList.sortedDescending().subList(0, 7)
    }

    var summary = 0 //sum of all categories on diagram
    var startAngle = 0f
    var position = 0

    values.forEach { summary += it }

    val items = mutableListOf<DrawItem>()//diagram arc items

    values.forEach { value ->
        val onePart = 360f / summary.toFloat()
        val sweepAngle = value.toFloat() * onePart
        items.add(
            DrawItem(
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                color = Data.getColors()[position]
            )
        )
        startAngle += sweepAngle
        position++
    }
    DrawArc(
        drawItems = items
    )
}

@Composable
private fun DrawArc(
    modifier: Modifier = Modifier,
    drawItems: List<DrawItem>
) {
    Canvas(modifier = modifier) {
        //calculate center of circle
        val centerX = (size.width / 2 - drawItems[0].radius)
        val centerY = (size.height / 2 - drawItems[0].radius)

        //draw arc for each value
        var angle = 0f
        val radius = drawItems[0].radius+25f
        drawItems.forEach { drawItem ->
            drawArc(
                color = drawItem.color,
                startAngle = drawItem.startAngle,
                sweepAngle = drawItem.sweepAngle,
                useCenter = false,
                topLeft = Offset(x = centerX, y = centerY),
                size = Size(drawItem.radius * 2, drawItem.radius * 2),
                style = Stroke(50f)
            )

            drawArc(
                color = Color.White,
                startAngle = drawItem.startAngle,
                sweepAngle = 1f,
                useCenter = false,
                topLeft = Offset(x = centerX, y = centerY),
                size = Size(drawItem.radius * 2, drawItem.radius * 2),
                style = Stroke(100f)
            )

            angle += drawItem.sweepAngle

            val rad = ((angle - drawItem.sweepAngle / 2) * 2 * Math.PI / 360).toFloat()
            val x = radius * cos(rad) + size.width / 2
            val y = radius * sin(rad) + size.height / 2
            drawCircle(
                color = drawItem.color,
                radius = 10f,
                center = Offset(x = x, y = y)
            )
            drawLine(
                color = drawItem.color,
                strokeWidth = 4f,
                start = Offset(x = x, y = y),
                end = Offset(x = x+ sign(cos(rad))*200, y = y)
            )
        }
    }
}