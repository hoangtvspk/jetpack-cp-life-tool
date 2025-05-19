package bui.dev.bujispinwheel.ui.qr_scanner.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavController
import bui.dev.bujispinwheel.R
import java.net.URLEncoder

@Composable
fun QRScannerScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    var qrResult by remember() { mutableStateOf<String?>(null) }
    var frameRect by remember() { mutableStateOf<Rect?>(null) }
    // previewSize lưu kích thước thực tế của PreviewView trên màn hình
    val previewSize = remember() { mutableStateOf(IntSize(1, 1)) }
    var isScanning by remember() { mutableStateOf(true) }
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Hiển thị camera và thực hiện quét QR
         if (hasCameraPermission) {
        CameraPreview(
            isScanning = isScanning,
            onQrCodeScanned = { qrText ->
                qrResult = qrText
                isScanning = false
                val encoded = URLEncoder.encode(qrResult, "UTF-8")
                navController.navigate("qr_result/$encoded")
            },
            frameRect = frameRect,
            previewSize = previewSize
        )} else {
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
        // Overlay + khung quét
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
                    .align(Alignment.Center)
                    .onGloballyPositioned { layoutCoordinates ->
                        // Lấy vị trí và kích thước khung quét trên màn hình
                        val position = layoutCoordinates.positionInRoot()
                        val size = layoutCoordinates.size
                        frameRect = Rect(
                            position.x.toInt(),
                            position.y.toInt(),
                            (position.x + size.width).toInt(),
                            (position.y + size.height).toInt()
                        )
                    }
            ) {
                // Vẽ border cho khung quét
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = Color.Cyan,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }
        }
       Column (
           modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.Bottom,
           horizontalAlignment = Alignment.End
       ) {
           Image(
               painter = painterResource(id = R.drawable.qr_panda_guiding),
               contentDescription = "Background",
               modifier = Modifier.fillMaxHeight(1/4f).padding(end = 20.dp, bottom = 20.dp),
               alignment = Alignment.BottomEnd
           )
           Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
       }
    }
}

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
