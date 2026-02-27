package com.financemanager.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.financemanager.R
import com.financemanager.data.db.Transaction
import com.financemanager.data.db.TransactionCategory
import com.financemanager.databinding.ItemTransactionBinding

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(t: Transaction) {
            binding.tvTitle.text = t.title
            binding.tvDate.text = t.date
            val sign = if (t.category == TransactionCategory.INCOME) "+" else "-"
            binding.tvAmount.text = "$sign${String.format("%.2f", t.amount)} ₽"
            val color = if (t.category == TransactionCategory.INCOME)
                binding.root.context.getColor(R.color.income_color)
            else binding.root.context.getColor(R.color.expense_color)
            binding.tvAmount.setTextColor(color)
            val catRes = if (t.category == TransactionCategory.INCOME)
                R.string.income else R.string.expense
            binding.tvCategory.text = binding.root.context.getString(catRes)
            binding.root.setOnClickListener { onItemClick(t) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(a: Transaction, b: Transaction) = a.id == b.id
        override fun areContentsTheSame(a: Transaction, b: Transaction) = a == b
    }
}
