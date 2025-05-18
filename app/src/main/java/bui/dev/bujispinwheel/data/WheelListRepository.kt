package bui.dev.bujispinwheel.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WheelListRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("wheel_lists", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveWheelList(wheelList: WheelList) {
        val existingLists = getWheelLists().toMutableList()
        existingLists.add(wheelList)
        val json = gson.toJson(existingLists)
        sharedPreferences.edit().putString("lists", json).apply()
    }

    fun updateWheelList(wheelList: WheelList) {
        val existingLists = getWheelLists().toMutableList()
        val index = existingLists.indexOfFirst { it.id == wheelList.id }
        if (index != -1) {
            existingLists[index] = wheelList
            val json = gson.toJson(existingLists)
            sharedPreferences.edit().putString("lists", json).apply()
        }
    }

    fun getWheelListById(id: String): WheelList? {
        return getWheelLists().find { it.id == id }
    }

    fun deleteWheelList(wheelList: WheelList) {
        val existingLists = getWheelLists().toMutableList()
        existingLists.removeAll { it.id == wheelList.id }
        val json = gson.toJson(existingLists)
        sharedPreferences.edit().putString("lists", json).apply()
    }

    fun getWheelLists(): List<WheelList> {
        val json = sharedPreferences.getString("lists", "[]")
        val type = object : TypeToken<List<WheelList>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
} 