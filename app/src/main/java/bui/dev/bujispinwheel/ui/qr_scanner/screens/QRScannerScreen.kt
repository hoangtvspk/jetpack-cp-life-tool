package bui.dev.bujispinwheel.ui.qr_scanner.screens

import QRFromGalleryButton
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bui.dev.bujispinwheel.R
import bui.dev.bujispinwheel.ui.qr_scanner.components.CameraPreview
import bui.dev.bujispinwheel.ui.qr_scanner.components.ScanningBox
import java.net.URLEncoder

@kotlin.OptIn(ExperimentalMaterial3Api::class)
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
        ScanningBox  {
                position, size ->
            frameRect = Rect(
                position.x.toInt(),
                position.y.toInt(),
                (position.x + size.width).toInt(),
                (position.y + size.height).toInt()
            )
        }

        // title
        Column (
            modifier = Modifier
                .zIndex(2f),
        ) {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Quét mã QR", fontSize = 14.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    QRFromGalleryButton{ it ->
                        qrResult = it
                        isScanning = false
                        val encoded = URLEncoder.encode(qrResult, "UTF-8")
                        navController.navigate("qr_result/$encoded")
                    }
                }
            )
        }

        // guiding
        Column (
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f),
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
