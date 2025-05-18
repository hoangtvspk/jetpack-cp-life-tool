package bui.dev.bujispinwheel.ui.wheelspin.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bui.dev.bujispinwheel.R
import bui.dev.bujispinwheel.data.WheelList
import bui.dev.bujispinwheel.data.WheelListRepository
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import bui.dev.bujispinwheel.ui.wheelspin.components.SavedListsDrawer
import bui.dev.bujispinwheel.ui.wheelspin.components.WheelCanvas
import bui.dev.bujispinwheel.ui.wheelspin.components.WheelResult


data class SliceStyle(
    val backgroundColor: Color,
    val textColor: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinWheel(
    modifier: Modifier = Modifier,
    items: List<String> = listOf(),
    navController: NavController
) {
    var rotation by remember { mutableStateOf(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }
    var pointerBounceTrigger by remember { mutableStateOf(false) }
    var currentInput by remember { mutableStateOf("") }
    var optionsText by remember { mutableStateOf(items.joinToString("\n")) }
    var options by remember { mutableStateOf(items) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var listName by remember { mutableStateOf("") }
    var currentListId by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val repository = remember { WheelListRepository(context) }
    val savedLists = remember { mutableStateOf(repository.getWheelLists()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 3000),
        label = "wheelRotation",
        finishedListener = {
            isSpinning = false
            val finalRotation = (rotation % 360f + 360f) % 360f
            val angleAtPointer = finalRotation % 360f
            val sliceAngle = 360f / options.size
            val selectedIndex = (options.size - 1 - (angleAtPointer / sliceAngle).toInt()) % options.size
            result = options[selectedIndex]
            showResult = true
        }
    )


    val sliceColors = listOf(
        SliceStyle(
            backgroundColor = Color(0xFFCCE490), // Xanh lá non
            textColor = "#3D3D3D"         // Xám đậm
        ),
        SliceStyle(
            backgroundColor = Color(0xFFFFD59E), // Vàng pastel
            textColor = "#5A3A00"       // Nâu đậm
        ),
        SliceStyle(
            backgroundColor = Color(0xFFA9C18F), // Xanh ô liu nhạt
            textColor = "#2F2F2F"       // Xám đậm
        ),
        SliceStyle(
            backgroundColor = Color(0xFFFAB857), // Cam sáng
            textColor = "#4B2800"        // Nâu đậm
        )
    )
    val borderColor = Color(0xFFFFB74D)
    val pointerColor = Color(0xFFD32F2F)
    val hubColor = Color(0xFFAAC290)
    val hubStrokeColor = Color(0xFF7E9268)
    val configuration = LocalConfiguration.current
    val titleColor = Color(0xFF5D4037)
    val fillColor = Color(0xFFFFE5A1)


    SavedListsDrawer(
        drawerState = drawerState,
        scope = scope,
        savedLists = savedLists.value,
        onListSelected = { wheelList ->
            options = wheelList.items
            optionsText = wheelList.items.joinToString("\n")
            currentListId = wheelList.id
            listName = wheelList.name
        },
        onDeleteList = { wheelList ->
            repository.deleteWheelList(wheelList)
            savedLists.value = repository.getWheelLists()
            if (currentListId == wheelList.id) {
                currentListId = null
                listName = ""
            }
        },
        onEditList = { listId ->
            scope.launch {
                drawerState.close()
                navController.navigate("edit_wheel_list/$listId")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.pressed }) {
                                focusManager.clearFocus()
                            }
                        }
                    }
                }
                .background(
                    Color(0xFFBFCB8E)
                )
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.popBackStack(
                        route = "home",
                        inclusive = false
                    )
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = titleColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    "Spin Wheel",
                    fontSize = 24.sp,
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    if (savedLists.value.isNotEmpty()) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },

                            ) {
                            Icon(
                                painter = painterResource(id = R.drawable.history),
                                contentDescription = "Danh sách đã lưu",
                                tint = titleColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomEnd).fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .aspectRatio(584 / 607f)

                ) {
                    Column {
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.wheel_input_background),
                            contentDescription = "Background",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = 400 / 584f)
                            .align(Alignment.BottomCenter)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {


                            OutlinedTextField(
                                value = optionsText,
                                onValueChange = {
                                    optionsText = it
                                    options = optionsText.split("\n")
                                        .map { line -> line.trim() }
                                        .filter { line -> line.isNotEmpty() }
                                },
                                label = { Text("Danh sách lựa chọn") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(400 / 145f),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = false,
                                maxLines = 10,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                )
                            )

                            Box(
                                modifier = Modifier.fillMaxWidth().aspectRatio(400 / 130f)
                                    .padding(end = 4.dp, top = 4.dp)
                            ) {
                                Row(modifier = Modifier.align(Alignment.TopEnd)) {
                                    Icon(
                                        imageVector = Icons.Filled.Create,
                                        contentDescription = "Back",
                                        tint = Color(0xFF4A90E2),
                                        modifier = Modifier.size(14.dp)

                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        if (currentListId != null) "Cập nhật" else "Lưu",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4A90E2),
                                        textDecoration = TextDecoration.Underline,
                                        style = TextStyle(

                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        ),
                                        modifier = Modifier
                                            .clickable { showSaveDialog = true }

                                    )
                                }
                            }


                        }
                    }
                }
                Column(
                    modifier = modifier.fillMaxSize().background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    Box(
                        modifier = Modifier
                            .width((if (configuration.screenWidthDp > 370) 380.0 else configuration.screenWidthDp * 0.7).dp)
                            .aspectRatio(1f)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        WheelCanvas(
                            options = options,
                            rotation = animatedRotation,
                            sliceColors = sliceColors,
                            borderColor = borderColor,
                            pointerColor = pointerColor,
                            hubColor = hubColor,
                            hubStrokeColor = hubStrokeColor,
                            fillColor = fillColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                showResult = false
                                rotation += Random.nextFloat() * 360f + 720f
                            }
                        },
                        enabled = options.isNotEmpty(),
                        modifier = Modifier
                            .size(width = 200.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF49200))
                    ) {
                        Text(
                            "Bắt đầu",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                }

            }

            // History button in top left corner

        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(if (currentListId != null) "Cập nhật danh sách" else "Lưu danh sách") },
            text = {
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("Tên danh sách") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (listName.isNotBlank()) {
                            if (currentListId != null) {
                                // Update existing list
                                repository.updateWheelList(
                                    WheelList(
                                        id = currentListId!!,
                                        name = listName,
                                        items = options
                                    )
                                )
                            } else {
                                // Create new list
                                repository.saveWheelList(
                                    WheelList(
                                        id = UUID.randomUUID().toString(),
                                        name = listName,
                                        items = options
                                    )
                                )
                            }
                            savedLists.value = repository.getWheelLists()
                            showSaveDialog = false
                        }
                    },
                    enabled = listName.isNotBlank()
                ) {
                    Text(if (currentListId != null) "Cập nhật" else "Lưu")
                }
            },
            dismissButton = {
                Button(onClick = { showSaveDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showResult) {
        pointerBounceTrigger = true
        WheelResult(
            modifier = modifier,
            result = result,
            onBack = {
                showResult = false
            },
            onRemoveResult = {
                if (options.contains(result)) {
                    // Create a new list without the item
                    val updatedOptions = options.toMutableList().apply {
                        remove(result)
                    }
                    options = updatedOptions // Update the state

                    // Update the optionsText as well
                    optionsText = updatedOptions.joinToString("\n")

                    // Optionally, you might want to close the result dialog after removal
                    showResult = false
                }
            }
        )
    }

}