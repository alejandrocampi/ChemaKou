package com.acampif.chemakou.history

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acampif.chemakou.data.CommandHistoryManager
import com.acampif.chemakou.history.HistoryAdapter
import com.acampif.chemakou.R
import com.acampif.chemakou.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyManager: CommandHistoryManager
    private lateinit var adapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHistoryBinding.bind(view)
        historyManager = CommandHistoryManager(requireContext())

        setupRecycler()
        loadHistory()

        binding.btnClearHistory.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Limpiar historial")
                .setMessage("¿Borrar todos los comandos?")
                .setPositiveButton("Sí") { _, _ ->
                    historyManager.clearHistory()
                    adapter.clear()
                    updateUI()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun setupRecycler() {
        adapter = HistoryAdapter(
            historyManager.getHistory().toMutableList()
        ) { command ->
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val removed = adapter.removeAt(pos)
                historyManager.removeCommand(removed)
                updateUI()
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvHistory)
    }

    private fun loadHistory() {
        updateUI()
    }

    private fun updateUI() {
        val hasData = adapter.itemCount > 0

        binding.rvHistory.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.txtNoCommands.visibility = if (hasData) View.GONE else View.VISIBLE
        binding.btnClearHistory.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.txtTapToListen.visibility = if (hasData) View.VISIBLE else View.GONE
    }
}