package bui.dev.bujispinwheel.navigation

import QRScannerResultScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import bui.dev.bujispinwheel.ui.home.screens.HomeScreen
import bui.dev.bujispinwheel.ui.qr_scanner.screens.QRScannerScreen
import bui.dev.bujispinwheel.ui.wheelspin.screens.SpinWheel
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

@Composable
fun Navigation() {
    val navController = rememberNavController()
    var scannerResetKey by remember { mutableStateOf(0) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("spin_wheel") {
            SpinWheel(navController = navController)
        }
        composable("qr_scanner") {
            QRScannerScreen(
                navController = navController,
            )
        }
        composable("qr_result/{qrResult}") { backStackEntry ->
            val qrResult = backStackEntry.arguments?.getString("qrResult")
            QRScannerResultScreen(
                navController = navController,
                qrResult = qrResult ?: "",

            )
        }
    }
}
