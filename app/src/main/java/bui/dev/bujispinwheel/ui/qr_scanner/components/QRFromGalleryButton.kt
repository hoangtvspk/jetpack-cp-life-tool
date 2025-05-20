import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import bui.dev.bujispinwheel.R

@Composable
fun QRFromGalleryButton(
    onQrScanned: (String) -> Unit
) {
    val context = LocalContext.current
    var showErrorDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val image = InputImage.fromFilePath(context, it)
                val scanner = BarcodeScanning.getClient()
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isEmpty()) {
                            showErrorDialog = true
                        } else {
                            for (barcode in barcodes) {
                                barcode.rawValue?.let { value ->
                                    onQrScanned(value)
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        showErrorDialog = true
                    }
            } catch (e: Exception) {
                showErrorDialog = true
            }
        }
    }

    // 👉 IconButton để chọn ảnh
    IconButton(onClick = {
        launcher.launch("image/*")
    }) {
        Icon(
            tint = Color.White,
            painter = painterResource(R.drawable.add_photo),
            contentDescription = "Upload image"
        )
    }

    // 👉 Dialog báo lỗi nếu có vấn đề
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Lỗi") },
            text = { Text("Không tìm thấy mã QR hợp lệ trong ảnh.") }
        )
    }
}