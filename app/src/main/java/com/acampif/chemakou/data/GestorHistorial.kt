package com.acampif.chemakou.data

import android.content.Context

class GestorHistorial(context: Context) {

    private val prefs = context.getSharedPreferences("command_history", Context.MODE_PRIVATE)

    fun guardarComando(command: String) {
        val history = obtenerHistorial().toMutableList()
        history.add(0, command)

        prefs.edit()
            .putStringSet("history", history.toSet())
            .apply()
    }

    fun obtenerHistorial(): MutableList<String> {
        return prefs.getStringSet("history", emptySet())?.toMutableList() ?: mutableListOf()
    }

    fun eliminarComando(command: String) {
        val history = obtenerHistorial()
        history.remove(command)
        prefs.edit().putStringSet("history", history.toSet()).apply()
    }

    fun limpiarHistorial() {
        prefs.edit().clear().apply()
    }
}