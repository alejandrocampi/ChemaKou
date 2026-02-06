package com.acampif.chemakou.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acampif.chemakou.databinding.ItemHistoryCommandBinding

class HistoryAdapter(
    private val commands: MutableList<String>,
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val command = commands[position]
        holder.binding.txtCommand.text = command
        holder.binding.root.setOnClickListener {
            onClick(command)
        }
    }

    override fun getItemCount(): Int = commands.size

    fun removeAt(position: Int): String {
        val removed = commands[position]
        commands.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }

    fun clear() {
        commands.clear()
        notifyDataSetChanged()
    }
}