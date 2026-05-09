package com.acampif.chemakou.history

import android.app.AlertDialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acampif.chemakou.data.GestorHistorial
import com.acampif.chemakou.R
import com.acampif.chemakou.databinding.FragmentHistoryBinding
import java.util.Locale

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var gestorHistorial: GestorHistorial
    private lateinit var adaptador: HistoryAdapter
    private lateinit var textToSpeech: TextToSpeech

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHistoryBinding.bind(view)
        gestorHistorial = GestorHistorial(requireContext())

        textToSpeech = TextToSpeech(requireContext()) {
            textToSpeech.language = Locale.getDefault()
        }

        configurarRecycler()
        cargarHistorial()

        binding.btnClearHistory.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Limpiar historial")
                .setMessage("¿Borrar todos los comandos?")
                .setPositiveButton("Sí") { _, _ ->
                    gestorHistorial.limpiarHistorial()
                    adaptador.limpiar()
                    actualizarUI()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun configurarRecycler() {
        adaptador = HistoryAdapter(
            gestorHistorial.obtenerHistorial().toMutableList()
        ) { comando ->
            hablar(comando)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adaptador

        val gestoSwipe = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posicion = viewHolder.adapterPosition
                val eliminado = adaptador.eliminarEn(posicion)
                gestorHistorial.eliminarComando(eliminado)
                actualizarUI()
            }
        }

        ItemTouchHelper(gestoSwipe).attachToRecyclerView(binding.rvHistory)
    }

    private fun cargarHistorial() {
        actualizarUI()
    }

    private fun actualizarUI() {
        val hayDatos = adaptador.itemCount > 0

        binding.rvHistory.visibility = if (hayDatos) View.VISIBLE else View.GONE
        binding.txtNoCommands.visibility = if (hayDatos) View.GONE else View.VISIBLE
        binding.btnClearHistory.visibility = if (hayDatos) View.VISIBLE else View.GONE
        binding.txtTapToListen.visibility = if (hayDatos) View.VISIBLE else View.GONE
    }

    private fun hablar(texto: String) {
        textToSpeech.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textToSpeech.shutdown()
    }
}