
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bui.dev.bujispinwheel.R

@Composable
fun QRScannerResultScreen(
    navController: NavController,
    qrResult: String,
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFBFCB8E)), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.wheel_panda_result),
            contentDescription = "Background",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopStart
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth().aspectRatio(530/795f),
        ) {
            Box(modifier = Modifier.fillMaxHeight(90/795f))
            Text(text = qrResult, color = Color.Black, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            Row {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B7346)),
                    onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("QR Result", qrResult)
                    clipboard.setPrimaryClip(clip)
                }) {
                    Text("Sao chép", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B7346)),
                    onClick = {
                    navController.popBackStack()
                }) {
                    Text("Quay về", fontSize = 12.sp)
                }
            }
        }
    }
}