package com.financemanager.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.financemanager.R
import com.financemanager.data.db.AppDatabase
import com.financemanager.data.db.Transaction
import com.financemanager.data.db.TransactionCategory
import com.financemanager.data.repository.TransactionRepository
import com.financemanager.databinding.FragmentTransactionDetailBinding
import com.financemanager.viewmodel.TransactionViewModel
import com.financemanager.viewmodel.TransactionViewModelFactory
import kotlinx.coroutines.launch

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by activityViewModels {
        val db = AppDatabase.getDatabase(requireContext())
        TransactionViewModelFactory(TransactionRepository(db.transactionDao()))
    }

    private val args: TransactionDetailFragmentArgs by navArgs()
    private var transaction: Transaction? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            transaction = viewModel.getById(args.transactionId)
            transaction?.let { t ->
                binding.tvTitle.text = t.title
                binding.tvDescription.text = t.description
                binding.tvDate.text = t.date
                val sign = if (t.category == TransactionCategory.INCOME) "+" else "-"
                binding.tvAmount.text = "$sign${String.format("%.2f", t.amount)} ₽"
                binding.tvAmount.setTextColor(
                    if (t.category == TransactionCategory.INCOME)
                        requireContext().getColor(R.color.income_color)
                    else requireContext().getColor(R.color.expense_color)
                )
                binding.tvCategory.text = getString(
                    if (t.category == TransactionCategory.INCOME) R.string.income else R.string.expense
                )
            }
        }

        binding.btnEdit.setOnClickListener {
            val action = TransactionDetailFragmentDirections
                .actionTransactionDetailFragmentToAddEditTransactionFragment(args.transactionId)
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            transaction?.let { t ->
                viewModel.delete(t)
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
