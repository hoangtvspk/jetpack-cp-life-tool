package bui.dev.bujispinwheel.data

import java.util.UUID

data class WheelList(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val items: List<String>
) 