package bui.dev.bujispinwheel.ui.home.screens

import FeatureType
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import bui.dev.bujispinwheel.R
import bui.dev.bujispinwheel.ui.home.components.FeatureCard
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val features = listOf(
        FeatureType(R.string.feature_spin_wheel, iconResId = R.drawable.home_wheel_icon, backgroundColor = Color(0xFFA3D977), textColor = Color(0xFF1B4300), onClick = {
            navController.navigate("spin_wheel");
        }),
        FeatureType(R.string.feature_scan_qr, iconResId =   R.drawable.home_qr_icon,  backgroundColor = Color(0xFFFFE082), textColor = Color(0xFF664D00)),
        FeatureType(R.string.feature_image_to_text,iconResId =   R.drawable.home_scan_text_icon,backgroundColor = Color(0xFF907161), textColor = Color(0xFFFFFFFF)),
        FeatureType(R.string.feature_countdown, iconResId =   R.drawable.home_countdown_icon, backgroundColor = Color(0xFFF4A261), textColor = Color(0xFF5B2B00)),
        FeatureType(R.string.feature_todo_calendar, iconResId =   R.drawable.home_todo_icon, backgroundColor = Color(0xFFB08968), textColor = Color(0xFF3E2C23)),
        FeatureType(R.string.feature_fake_sound, iconResId =   R.drawable.home_fake_sound, backgroundColor = Color(0xFFD6A35D), textColor = Color(0xFF3E2500)),
    )
    val configuration = LocalConfiguration.current

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE2F1C5)),
        
    ) {
        // Background Image
        Box(modifier = Modifier.padding(all = 16.dp)){
            Image(
                painter = painterResource(id = R.drawable.home_background),
                contentDescription = "Background",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopStart
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x33000000)), // 0x80 = alpha 50%
                        startY = 800f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (configuration.screenWidthDp > 400) 2 else 3),
                contentPadding = PaddingValues(if (configuration.screenWidthDp > 400)  8.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(if (configuration.screenWidthDp > 400) 0.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(if (configuration.screenWidthDp > 400) 16.dp  else 0.dp),
            ) {
                items(features) { feature ->
                    FeatureCard(feature, showTitle = configuration.screenWidthDp > 400)
                }
            }
        }

    }
}
