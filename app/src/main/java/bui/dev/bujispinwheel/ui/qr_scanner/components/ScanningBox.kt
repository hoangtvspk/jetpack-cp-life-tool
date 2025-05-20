package bui.dev.bujispinwheel.ui.qr_scanner.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ScanningBox(onGloballyPositioned: (position : Offset, size: IntSize) -> Unit){

    val frameBoxSize = remember { mutableStateOf(IntSize(0, 0)) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        val frameSize = 250.dp
        // Khung quét (hình vuông ở giữa màn hình)
        Box(
            modifier = Modifier
                .size(frameSize)
                .zIndex(2f)
                .align(Alignment.Center)
                .onGloballyPositioned { layoutCoordinates ->
                    // Lấy vị trí và kích thước khung quét trên màn hình
                    val position = layoutCoordinates.positionInRoot()
                    val size = layoutCoordinates.size
                    frameBoxSize.value = layoutCoordinates.size
                    onGloballyPositioned(position, size)

                }
        ) {
            val scanLineY = remember { Animatable(0f) }
            val frameHeight = frameBoxSize.value.height.toFloat()
            val frameWidth = frameBoxSize.value.width.toFloat()
            LaunchedEffect(frameHeight) {
                if (frameHeight > 0f) {
                    while (true) {
                        scanLineY.animateTo(
                            targetValue = frameHeight,
                            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
                        )
                        scanLineY.snapTo(0f)
                    }
                }
            }
            // Vẽ border cho khung quét
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 2.dp.toPx()
                val cornerRadius = 24.dp.toPx() // Độ bo nhẹ
                val sizeOffset = -20f
                val cornerOffset = sizeOffset + cornerRadius // Độ dài đoạn thẳng vuông góc
                val cornerLength = cornerOffset + 30.dp.toPx() // Độ dài đoạn thẳng vuông góc

                val w = size.width
                val h = size.height
                val color = Color(0xFF00BFAE)

                // Top-left
                // Ngang
                drawLine(color, Offset(cornerOffset, sizeOffset), Offset(cornerLength, sizeOffset), stroke)
                // Dọc
                drawLine(color, Offset(sizeOffset, cornerOffset), Offset(sizeOffset, cornerLength), stroke)
                // Cung
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(sizeOffset, sizeOffset),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = stroke)
                )

                // Top-right
                drawLine(color, Offset(w - cornerOffset, sizeOffset), Offset(w - cornerLength, sizeOffset), stroke)
                drawLine(color, Offset(w - sizeOffset, cornerOffset), Offset(w - sizeOffset, cornerLength), stroke)
                drawArc(
                    color = color,
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w - cornerRadius * 2 - sizeOffset, sizeOffset),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = stroke)
                )

                // Bottom-left
                drawLine(color, Offset(cornerOffset, h - sizeOffset), Offset(cornerLength, h - sizeOffset), stroke)
                drawLine(color, Offset(sizeOffset, h - cornerOffset), Offset(sizeOffset, h - cornerLength), stroke)
                drawArc(
                    color = color,
                    startAngle = 90f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(sizeOffset, h - cornerRadius * 2 - sizeOffset),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = stroke)
                )

                // Bottom-right
                drawLine(color, Offset(w - cornerOffset, h - sizeOffset), Offset(w - cornerLength , h - sizeOffset), stroke)
                drawLine(color, Offset(w - sizeOffset, h - cornerOffset), Offset(w - sizeOffset, h - cornerLength), stroke)
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w - cornerRadius * 2  - sizeOffset, h - cornerRadius * 2 - sizeOffset),
                    size = Size(cornerRadius * 2, cornerRadius * 2  ),
                    style = Stroke(width = stroke)
                )

                drawLine(
                    color = Color(0xFF00BFAE),
                    start = Offset(0f + sizeOffset*2, scanLineY.value),
                    end = Offset(frameWidth - sizeOffset*2, scanLineY.value),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Vẽ 4 hình chữ nhật đen mờ xung quanh khung quét
        Canvas(modifier = Modifier.fillMaxSize().zIndex(1f)) {
            val frameWidth = frameSize.toPx()
            val frameHeight = frameSize.toPx()
            val screenWidth = size.width
            val screenHeight = size.height
            val frameX = (screenWidth - frameWidth) / 2
            val frameY = (screenHeight - frameHeight) / 2

            // Vẽ 4 hình chữ nhật đen mờ
            // Hình chữ nhật phía trên
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(0f, 0f),
                size = Size(screenWidth, frameY)
            )
            // Hình chữ nhật phía dưới
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(0f, frameY + frameHeight),
                size = Size(screenWidth, screenHeight - (frameY + frameHeight))
            )
            // Hình chữ nhật bên trái
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(0f, frameY),
                size = Size(frameX, frameHeight)
            )
            // Hình chữ nhật bên phải
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(frameX + frameWidth, frameY),
                size = Size(screenWidth - (frameX + frameWidth), frameHeight)
            )
        }
    }
}