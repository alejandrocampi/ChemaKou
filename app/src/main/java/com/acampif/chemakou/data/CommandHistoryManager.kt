package com.acampif.chemakou.data

import android.content.Context

class CommandHistoryManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "command_history",
        Context.MODE_PRIVATE
    )

    fun saveCommand(command: String) {
        val history = getHistory().toMutableList()
        history.add(0, command)

        prefs.edit()
            .putString("history", history.joinToString("|"))
            .apply()
    }

    fun getHistory(): List<String> {
        val data = prefs.getString("history", "") ?: ""
        if (data.isEmpty()) return emptyList()
        return data.split("|")
    }

    fun removeCommand(command: String) {
        val history = getHistory().toMutableList()
        history.remove(command)
        prefs.edit()
            .putString("history", history.joinToString("|"))
            .apply()
    }

    fun clearHistory() {
        prefs.edit().clear().apply()
    }
}