package bui.dev.bujispinwheel.ui.wheelspin.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import bui.dev.bujispinwheel.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun WheelResult(onBack: () -> Unit, onRemoveResult: () -> Unit ,modifier: Modifier, result: String){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)) // Nền mờ
            .clickable(onClick = onBack)
    ) {


        Image(
            painter = painterResource(id = R.drawable.wheel_panda_result_2),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = modifier.fillMaxSize().background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .aspectRatio(408 / 612f)){
                Column (
                    modifier = modifier.fillMaxSize().background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Box(modifier = Modifier.fillMaxHeight(fraction = 40/612f));
                    Box(modifier = Modifier
                        .fillMaxWidth(fraction = 300/408f)
                        .aspectRatio(300/139f)
                        .align(Alignment.CenterHorizontally)
                        ){
                        Column (
                            modifier = modifier.fillMaxSize().background(Color.Transparent),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AutoResizeText(
                                text = "🎉 $result 🎉",
                                maxFontSize = 32.sp,
                                minFontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        Column (
                            modifier = modifier.fillMaxSize().background(Color.Transparent),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Back",
                                    tint = Color(0xFFBA4242),
                                    modifier = Modifier.size(14.dp)

                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Xoá lựa chọn",
                                    fontSize = 12.sp,
                                    color = Color(0xFFBA4242),
                                    style = TextStyle(textDecoration = TextDecoration.Underline),
                                    modifier = Modifier.align(Alignment.CenterVertically).clickable{
                                        onRemoveResult()
                                    }

                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Back",
                                    tint = Color(0xFF4A90E2),
                                    modifier = Modifier.size(14.dp)

                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tiếp tục",
                                    color = Color(0xFF4A90E2),
                                    fontSize = 12.sp,
                                    style = TextStyle(textDecoration = TextDecoration.Underline),
                                    modifier = Modifier.align(Alignment.CenterVertically).clickable{
                                        onBack()
                                    }
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 32.sp,
    minFontSize: TextUnit = 12.sp,
    maxLines: Int = 1,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Center
) {
    var currentFontSize by remember { mutableStateOf(maxFontSize) }
    var readyToDraw by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier) {
        Text(
            text = text,
            fontSize = currentFontSize ,
            fontWeight = fontWeight,
            color = color,
            textAlign = textAlign,
            maxLines = if(currentFontSize == minFontSize) maxLines else 1,
            lineHeight = currentFontSize * 1.2,
            softWrap = currentFontSize == minFontSize,
            overflow = if(currentFontSize == minFontSize) TextOverflow.Ellipsis else TextOverflow.Clip ,
            onTextLayout = { result ->
                if (!readyToDraw) {
                    if (result.didOverflowWidth && currentFontSize > minFontSize) {
                        currentFontSize = (currentFontSize.value - 1).sp
                    } else {
                        readyToDraw = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}