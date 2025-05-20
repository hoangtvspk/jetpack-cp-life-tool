
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bui.dev.bujispinwheel.R
import bui.dev.bujispinwheel.ui.wheelspin.components.AutoResizeText

@Composable
fun QRScannerResultScreen(
    navController: NavController,
    qrResult: String,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFBFCB8E)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.wheel_panda_result_2),
            contentDescription = "Background",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopStart
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth(387/530f)
                .aspectRatio(387/795f)
        ) {
            Box(modifier = Modifier.fillMaxHeight(90/795f))
            Box(modifier = Modifier.padding(horizontal = 8.dp)){
                AutoResizeText(
                    text = qrResult,
                    maxFontSize = 32.sp,
                    minFontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF5D4037),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, end = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5C386)),
                    onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("QR Result", qrResult)
                    clipboard.setPrimaryClip(clip)
                }) {
                    Text("Sao chép", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3B665)),
                    onClick = {
                    navController.popBackStack()
                }) {
                    Text("Quay về", fontSize = 12.sp)
                }
            }
        }
    }
}