package bui.dev.bujispinwheel.ui.home.components

import FeatureType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp



@Composable
fun FeatureCard(feature: FeatureType, showTitle: Boolean = true) {
    Box(
        modifier = Modifier.padding(all = 12.dp)
    ){
        Card(
            modifier = Modifier
                .aspectRatio(1f)
                .clickable { feature.onClick() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = feature.backgroundColor),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (feature.iconResId != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = feature.iconResId),
                        contentDescription = stringResource(id = feature.nameRes),
                        modifier = Modifier.size(80.dp)
                    )
                } else if (feature.icon != null) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = stringResource(id = feature.nameRes),
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                if (showTitle){
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = feature.nameRes),
                        color = feature.textColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}