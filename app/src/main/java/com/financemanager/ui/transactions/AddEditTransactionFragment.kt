package com.financemanager.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.financemanager.databinding.FragmentAddEditTransactionBinding
import com.financemanager.viewmodel.TransactionViewModel
import com.financemanager.viewmodel.TransactionViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditTransactionFragment : Fragment() {

    private var _binding: FragmentAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by activityViewModels {
        val db = AppDatabase.getDatabase(requireContext())
        TransactionViewModelFactory(TransactionRepository(db.transactionDao()))
    }

    private val args: AddEditTransactionFragmentArgs by navArgs()
    private var existing: Transaction? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.transactionId != -1) {
            lifecycleScope.launch {
                existing = viewModel.getById(args.transactionId)
                existing?.let {
                    binding.etTitle.setText(it.title)
                    binding.etDescription.setText(it.description)
                    binding.etAmount.setText(it.amount.toString())
                    binding.etDate.setText(it.date)
                    if (it.category == TransactionCategory.INCOME) binding.rbIncome.isChecked = true
                    else binding.rbExpense.isChecked = true
                }
            }
        } else {
            binding.etDate.setText(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()))
            binding.rbExpense.isChecked = true
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        val date = binding.etDate.text.toString().trim()

        if (title.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show()
            return
        }
        val category = if (binding.rbIncome.isChecked) TransactionCategory.INCOME else TransactionCategory.EXPENSE
        val t = Transaction(
            id = existing?.id ?: 0,
            title = title, description = desc,
            amount = amount, date = date, category = category
        )
        if (existing != null) viewModel.update(t) else viewModel.insert(t)
        findNavController().navigateUp()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
