package bui.dev.bujispinwheel.ui.wheelspin.components

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.core.graphics.toColorInt
import bui.dev.bujispinwheel.R

@Composable
fun WheelCanvas(
    modifier: Modifier = Modifier,
    options: List<String>,
    rotation: Float,
    sliceColors: List<Color>,
    borderColor: Color,
    pointerColor: Color,
    hubColor: Color,
    hubStrokeColor: Color,
    fillColor: Color
) {
    val textMeasurer = rememberTextMeasurer()


    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2 * 0.95f
        val sliceAngle = 360f / options.size
        val wheelRotationOffset = -90f // Item 1 at top

        // Draw the fill
        drawCircle(
            color = fillColor,
            radius = radius,
            center = center
        )
        // Draw slices with alternating colors
        options.forEachIndexed { index, item ->
            val startAngle = index * sliceAngle + rotation + wheelRotationOffset
            val sweepAngle = sliceAngle
            drawArc(
                color = if(index == 0) fillColor else sliceColors[index % sliceColors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }

//        // Draw divider lines between sections
//        options.forEachIndexed { index, _ ->
//            val angle = index * sliceAngle + rotation + wheelRotationOffset
//            val angleInRadians = Math.toRadians(angle.toDouble())
//            val startX = center.x + (radius * 0.2f * cos(angleInRadians)).toFloat()
//            val startY = center.y + (radius * 0.2f * sin(angleInRadians)).toFloat()
//            val endX = center.x + (radius * cos(angleInRadians)).toFloat()
//            val endY = center.y + (radius * sin(angleInRadians)).toFloat()
//            if(options.size > 1)
//            {
//                drawLine(
//                    color = Color(0xFFFFD59E),
//                    start = Offset(startX, startY),
//                    end = Offset(endX, endY),
//                    strokeWidth = 3f
//                )
//            }
//        }

        // Draw border
        drawCircle(
            color = borderColor,
            radius = radius,
            center = center,
            style = Stroke(width = 8f)
        )





        // Draw text as a whole word, upright and centered in each slice
        options.forEachIndexed { index, item ->
            val startAngle = index * sliceAngle + rotation + wheelRotationOffset
            var fontSize = 22.sp
            var lineHeightPx = fontSize.toPx()
            val middleAngle = startAngle + sliceAngle / 2



            val chars = item.toCharArray()

            val fixedOffsetFromEdge = 40.dp.toPx()


            val textBlockHeight = lineHeightPx * chars.size

            val maxAllowHeight = radius * 0.9f
            val angleRad = (middleAngle) * PI / 180.0

            if (textBlockHeight > maxAllowHeight) {
                val scale = maxAllowHeight / textBlockHeight
                fontSize = (fontSize.value * scale).sp
                lineHeightPx = fontSize.toPx()
            }

            val paint = Paint().apply {
                color = "#5D4037".toColorInt()
                textSize = fontSize.toPx()
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }

            // Tính tổng độ cao dựa vào width của từng chữ
            val charWidths = chars.map { paint.measureText(it.toString()) }
            val totalHeight = charWidths.sum()

            // Nếu tổng vượt quá maxAllowHeight thì scale lại
            if (totalHeight > maxAllowHeight) {
                val scale = maxAllowHeight / totalHeight
                paint.textSize *= scale
            }

            // Recalculate charWidths after scaling
            val scaledCharWidths = chars.map { paint.measureText(it.toString()) }
            val adjustedBaseRadius = radius - fixedOffsetFromEdge
            var cumulativeOffset = 0f

            chars.forEachIndexed { index, c ->
                val offsetY = -totalHeight / 2 + cumulativeOffset + scaledCharWidths[index] /2
                cumulativeOffset += scaledCharWidths[index]

                val distanceFromCenter = adjustedBaseRadius + offsetY

                val x = center.x + distanceFromCenter * cos(angleRad)
                val y = center.y + distanceFromCenter * sin(angleRad)

                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(x.toFloat(), y.toFloat())
                    canvas.rotate(middleAngle.toFloat())
                    canvas.nativeCanvas.drawText(c.toString(), 0f, 0f, paint)
                    canvas.restore()
                }
            }
        }

        // Draw pointer at the top
        val pointerHeight = 32f
        val pointerWidth = 32f
        drawPolygon(
            color = pointerColor,
            points = listOf(
                Offset(center.x, center.y - radius * 0.18f - pointerHeight*4/5),
                Offset(center.x - pointerWidth / 2, center.y - radius * 0.18f  + pointerHeight/5 ),
                Offset(center.x + pointerWidth / 2, center.y - radius * 0.18f  + pointerHeight/5 ),
            ),
        )

        // Draw central hub
        drawCircle(
            color = hubColor,
            radius = radius * 0.18f,
            center = center
        )
        drawCircle(
            color = hubStrokeColor,
            radius = radius * 0.18f,
            center = center,
            style = Stroke(width = 6f)
        )

    }
    Image(
        painter = painterResource(id = R.drawable.wheel_panda_point),
        contentDescription = "Background",
        modifier = Modifier.width(30.dp)
    )
}

private fun DrawScope.drawPolygon(color: Color, points: List<Offset>) {
    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            lineTo(points[i].x, points[i].y)
        }
        close()
    }
    drawPath(path, color)
} 