package bui.dev.bujispinwheel.ui.qr_scanner.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import android.graphics.Rect as AndroidRect
import androidx.compose.ui.platform.LocalDensity
import android.graphics.RectF
import android.util.Log
import android.widget.Toast

@OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerScreen(
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // 1. State to hold the scanned QR value
    var scannedValue by remember { mutableStateOf<String?>(null) }

    // 2. Pass a lambda to update the state when a QR is scanned
    val onQrCodeScannedInternal: (String) -> Unit = { value ->
        scannedValue = value
        Log.d("QRScanner", "Scanned: $value")
        Toast.makeText(context, "Scanned: $value", Toast.LENGTH_SHORT).show()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    previewViewRef = previewView
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val barcodeScanner = BarcodeScanning.getClient()
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx), { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null && previewViewRef != null) {
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                barcodeScanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        val previewView = previewViewRef!!
                                        val viewWidth = previewView.width
                                        val viewHeight = previewView.height
                                        val boxSizePx = with(density) { 250.dp.toPx() }
                                        val scanBoxLeft = (viewWidth - boxSizePx) / 2f
                                        val scanBoxTop = (viewHeight - boxSizePx) / 2f
                                        val scanBoxRect = RectF(
                                            scanBoxLeft,
                                            scanBoxTop,
                                            scanBoxLeft + boxSizePx,
                                            scanBoxTop + boxSizePx
                                        )
                                        for (barcode in barcodes) {
                                            val qrBox = barcode.boundingBox // Android.graphics.Rect in image coordinates
                                            if (qrBox != null) {
                                                val mappedBox = mapImageRectToViewRect(qrBox, imageProxy, previewView)
                                                if (mappedBox != null && scanBoxRect.contains(mappedBox)) {
                                                    barcode.rawValue?.let { onQrCodeScannedInternal(it) }
                                                }
                                            }
                                        }
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        })

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Overlay: semi-transparent black with transparent cutout for the scanning box
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val overlayColor = Color.Black.copy(alpha = 0.6f)
                    val boxSize = 250.dp.toPx()
                    val boxLeft = (size.width - boxSize) / 2f
                    val boxTop = (size.height - boxSize) / 2f
                    val boxRect = androidx.compose.ui.geometry.Rect(boxLeft, boxTop, boxLeft + boxSize, boxTop + boxSize)

                    val path = androidx.compose.ui.graphics.Path().apply {
                        // Outer rectangle (whole screen)
                        addRect(androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height))
                        // Inner rectangle (the transparent box)
                        addRoundRect(
                            RoundRect(
                                rect = boxRect,
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                            )
                        )
                        fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                    }
                    drawPath(path, overlayColor, style = androidx.compose.ui.graphics.drawscope.Fill)
                }

                // Draw the border for the scanning box
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(250.dp)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 4.dp,
                            color = Color(0xFF00E0FF),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }

            // Show the scanned value as text under the scanning box
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(250.dp + 32.dp)) // 250dp for box, 32dp for spacing
                scannedValue?.let {
                    Text(
                        text = it,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                }
            }
        } else {
            // Permission denied UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera permission is required to scan QR codes.", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

// Helper to map image rect to PreviewView coordinates
private fun mapImageRectToViewRect(
    imageRect: AndroidRect,
    imageProxy: ImageProxy,
    previewView: PreviewView
): RectF? {
    val imageWidth = imageProxy.width.toFloat()
    val imageHeight = imageProxy.height.toFloat()
    val viewWidth = previewView.width.toFloat()
    val viewHeight = previewView.height.toFloat()
    if (imageWidth == 0f || imageHeight == 0f || viewWidth == 0f || viewHeight == 0f) return null

    // Assume FILL_CENTER scale type (default for PreviewView)
    val scale = maxOf(viewWidth / imageWidth, viewHeight / imageHeight)
    val scaledWidth = imageWidth * scale
    val scaledHeight = imageHeight * scale
    val dx = (viewWidth - scaledWidth) / 2f
    val dy = (viewHeight - scaledHeight) / 2f

    val left = imageRect.left * scale + dx
    val top = imageRect.top * scale + dy
    val right = imageRect.right * scale + dx
    val bottom = imageRect.bottom * scale + dy
    return RectF(left, top, right, bottom)
}

// Extension to check if a RectF is fully inside another RectF
private fun RectF.contains(inner: RectF): Boolean {
    return this.left <= inner.left && this.top <= inner.top &&
            this.right >= inner.right && this.bottom >= inner.bottom
}