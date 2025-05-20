package bui.dev.bujispinwheel.ui.qr_scanner.components

import android.graphics.Rect
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    isScanning: Boolean,
    onQrCodeScanned: (String) -> Unit,
    frameRect: Rect?,
    previewSize: MutableState<IntSize>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // PreviewView là view hiển thị camera của CameraX
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var analysisUseCase: ImageAnalysis? by remember { mutableStateOf(null) }

    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                // Lưu lại kích thước thực tế của PreviewView để tính toán scale
                previewSize.value = coordinates.size
            }
    ) { view ->
        val cameraProvider = cameraProviderFuture.get()
        // Khởi tạo preview cho camera
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(view.surfaceProvider)
        }
        // Khởi tạo use case cho phân tích hình ảnh (dùng để quét QR)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val scanner = BarcodeScanning.getClient()
        // Xử lý từng frame camera để quét QR
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            if (!isScanning) {
                imageProxy.close()
                return@setAnalyzer
            }
            val mediaImage = imageProxy.image
            // Chỉ xử lý khi có hình ảnh và đã xác định được khung quét + previewSize
            if (mediaImage != null && frameRect != null && previewSize.value.width > 1 && previewSize.value.height > 1) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            val box = barcode.boundingBox
                            if (box != null) {
                                // Tính tỷ lệ scale giữa ảnh camera và PreviewView
                                val scaleX = previewSize.value.width.toFloat() / image.width
                                val scaleY = previewSize.value.height.toFloat() / image.height
                                // Chuyển bounding box của QR code sang toạ độ trên PreviewView
                                val barcodeRect = Rect(
                                    (box.left * scaleX).toInt(),
                                    (box.top * scaleY).toInt(),
                                    (box.right * scaleX).toInt(),
                                    (box.bottom * scaleY).toInt()
                                )
                                // Kiểm tra mã QR có nằm hoàn toàn trong khung quét không
                                if (frameRect!!.contains(barcodeRect)) {
                                    onQrCodeScanned(barcode.rawValue ?: "")
                                    break
                                }
                            }
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
        // Gắn các use case vào camera
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalysis
        )
        analysisUseCase = imageAnalysis
    }
}
