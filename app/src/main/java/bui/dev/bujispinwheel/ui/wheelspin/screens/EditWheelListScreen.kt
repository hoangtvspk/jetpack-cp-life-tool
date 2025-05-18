package bui.dev.bujispinwheel.ui.wheelspin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bui.dev.bujispinwheel.data.WheelList
import bui.dev.bujispinwheel.data.WheelListRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWheelListScreen(
    listId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { WheelListRepository(context) }
    val wheelList = remember { repository.getWheelListById(listId) }
    
    var name by remember { mutableStateOf(wheelList?.name ?: "") }
    var optionsText by remember { mutableStateOf(wheelList?.items?.joinToString("\n") ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
            Text(
                "Chỉnh sửa danh sách",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên danh sách") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = optionsText,
            onValueChange = { optionsText = it },
            label = { Text("Danh sách (mỗi mục một dòng)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .then(
                    if (optionsText.isBlank()) Modifier.background(Color(0xFFF2F2F2)) else Modifier
                ),
            singleLine = false,
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val options = optionsText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
                if (name.isNotBlank() && options.isNotEmpty() && wheelList != null) {
                    repository.updateWheelList(wheelList.copy(name = name, items = options))
                    onNavigateBack()
                }
            },
            enabled = name.isNotBlank() && optionsText.isNotBlank(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lưu thay đổi")
        }
    }
} 