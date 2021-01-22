package com.app.calculator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.calculator.databinding.ItemOperationHistoryBinding

class OperationHistoryAdapter(private val operationEvents: OperationEvents) :
    RecyclerView.Adapter<OperationHistoryAdapter.OperationHistoryViewHolder>() {
    private var operationsHistory = mutableListOf<String>()

    inner class OperationHistoryViewHolder(private val itemBinding: ItemOperationHistoryBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(position: Int) {
            itemBinding.tvOperation.apply {
                text = operationsHistory[position]
                setOnClickListener {
                    operationEvents.clickedForUndo(position)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationHistoryViewHolder {
        val itemBinding =
            ItemOperationHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OperationHistoryViewHolder(itemBinding)
    }

    fun submitList(operations: List<String>) {
        operationsHistory.clear()
        operationsHistory.addAll(operations)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: OperationHistoryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = operationsHistory.size
}