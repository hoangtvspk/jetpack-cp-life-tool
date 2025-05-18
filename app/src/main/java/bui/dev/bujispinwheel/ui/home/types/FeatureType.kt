import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Định nghĩa type cho tính năng
data class FeatureType(
    val nameRes: Int,
    val icon: ImageVector? = null,
    val iconResId: Int? = null,
    val backgroundColor: Color,
    val textColor: Color = Color.White,
    val onClick: () -> Unit = {}
)