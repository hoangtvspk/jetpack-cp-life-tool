package bui.dev.bujispinwheel.ui.wheelspin.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bui.dev.bujispinwheel.data.WheelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedListsDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    savedLists: List<WheelList>,
    onListSelected: (WheelList) -> Unit,
    onDeleteList: (WheelList) -> Unit,
    onEditList: (String) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Danh sách đã lưu",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { scope.launch { drawerState.close() } }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Đóng")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (savedLists.isEmpty()) {
                        Text(
                            "Chưa có danh sách nào được lưu",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    } else {
                        savedLists.forEach { wheelList ->
                            ListItem(
                                headlineContent = { Text(wheelList.name) },
                                supportingContent = { Text("${wheelList.items.size} mục") },
                                trailingContent = {
                                    Row {
                                        IconButton(
                                            onClick = { onEditList(wheelList.id) }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                                        }
                                        IconButton(
                                            onClick = { onDeleteList(wheelList) }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Xóa")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onListSelected(wheelList)
                                        scope.launch { drawerState.close() }
                                    }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
} 