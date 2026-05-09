package com.acampif.chemakou.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acampif.chemakou.databinding.ItemHistoryCommandBinding

class HistoryAdapter(
    private val lista: MutableList<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryCommandBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryCommandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comando = lista[position]
        holder.binding.txtCommand.text = comando

        holder.itemView.setOnClickListener {
            onClick(comando)
        }
    }

    fun eliminarEn(pos: Int): String {
        val eliminado = lista[pos]
        lista.removeAt(pos)
        notifyItemRemoved(pos)
        return eliminado
    }

    fun limpiar() {
        lista.clear()
        notifyDataSetChanged()
    }
}