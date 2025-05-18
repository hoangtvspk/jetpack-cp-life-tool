package bui.dev.bujispinwheel.ui.wheelspin.components

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.core.graphics.toColorInt
import bui.dev.bujispinwheel.R
import bui.dev.bujispinwheel.ui.wheelspin.screens.SliceStyle


@Composable
fun WheelCanvas(
    modifier: Modifier = Modifier,
    options: List<String>,
    rotation: Float,
    sliceColors: List<SliceStyle>,
    borderColor: Color,
    pointerColor: Color,
    hubColor: Color,
    hubStrokeColor: Color,
    fillColor: Color
) {
    val context = LocalContext.current
    val customTypeface = ResourcesCompat.getFont(context, R.font.be_vietnam_pro_semibold)
    val configuration = LocalConfiguration.current

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
                color = if(index == 0) fillColor else sliceColors[index % sliceColors.size].backgroundColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }


        // Draw the stroke
        drawCircle(
            color = borderColor,
            radius = radius,
            center = center,
            style = Stroke(width = 8f)
        )

        // Vẽ text từng slice
        options.forEachIndexed { index, item ->
            val startAngle = index * sliceAngle + rotation + wheelRotationOffset
            val middleAngle = startAngle + sliceAngle / 2

            var fontSize = 22.sp
            val paint = Paint().apply {
                color = (if(index == 0) ("#5A3A00").toColorInt() else sliceColors[index % sliceColors.size].textColor.toColorInt())
                textSize = fontSize.toPx()

                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                typeface = customTypeface
            }

            val chars = item.toCharArray()
            val charHeights = chars.map { paint.measureText(it.toString()) }
            val totalTextHeight = charHeights.sum()

            // Giới hạn vùng hiển thị chữ: cách mép ngoài 40dp
            val maxTextHeight = radius * 0.7f
            if (totalTextHeight > maxTextHeight) {
                val scale = maxTextHeight / totalTextHeight
                paint.textSize *= scale
            }

            // Cập nhật lại chiều cao sau khi scale
            val scaledCharHeights = chars.map { paint.measureText(it.toString()) }
            val totalScaledHeight = scaledCharHeights.sum()
            val targetRadius = radius - 40.dp.toPx()  // 👈 Khoảng cách chữ cuối cùng tới viền

            var offsetYAcc = -totalScaledHeight / 2f
            val angleRad = Math.toRadians(middleAngle.toDouble())

            chars.forEachIndexed { i, c ->
                val charHeight = scaledCharHeights[i]
                val yOffsetInText = offsetYAcc + charHeight / 2f
                offsetYAcc += charHeight

                val distanceFromCenter = targetRadius - (totalScaledHeight / 2f) + yOffsetInText + 30.dp.toPx()

                val x = center.x + distanceFromCenter * cos(angleRad).toFloat()
                val y = center.y + distanceFromCenter * sin(angleRad).toFloat()

                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(x, y)
                    canvas.rotate(middleAngle.toFloat())
                    canvas.nativeCanvas.drawText(c.toString(), 0f, -paint.fontMetrics.ascent / 2f, paint)
                    canvas.restore()
                }
            }
        }

        if(options.isNotEmpty()){
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

    }
    if(options.isNotEmpty()){
        Image(
            painter = painterResource(id = R.drawable.wheel_panda_point),
            contentDescription = "Background",
            modifier = Modifier.width(if(configuration.screenWidthDp > 400) 30.dp else 22.dp)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.wheel_panda_guiding),
            contentDescription = "Background",
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
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