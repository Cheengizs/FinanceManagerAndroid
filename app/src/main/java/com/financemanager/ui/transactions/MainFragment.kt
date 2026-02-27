package com.financemanager.ui.transactions

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.financemanager.R
import com.financemanager.data.db.AppDatabase
import com.financemanager.data.repository.TransactionRepository
import com.financemanager.databinding.FragmentMainBinding
import com.financemanager.viewmodel.TransactionViewModel
import com.financemanager.viewmodel.TransactionViewModelFactory

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by activityViewModels {
        val db = AppDatabase.getDatabase(requireContext())
        TransactionViewModelFactory(TransactionRepository(db.transactionDao()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Settings menu icon
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (menuItem.itemId == R.id.action_settings) {
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                    true
                } else false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val adapter = TransactionAdapter { transaction ->
            val action = MainFragmentDirections
                .actionMainFragmentToTransactionDetailFragment(transaction.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val swipeHandler = object : androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT or androidx.recyclerview.widget.ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    val transaction = adapter.currentList[position]
                    viewModel.delete(transaction)
                    com.google.android.material.snackbar.Snackbar.make(binding.root, R.string.transaction_deleted, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo) { viewModel.insert(transaction) }
                        .show()
                }
            }
        }
        androidx.recyclerview.widget.ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerView)

        viewModel.allTransactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            val amount = balance ?: 0.0
            binding.tvBalance.text = String.format("%.2f ₽", amount)
            binding.tvBalance.setTextColor(
                if (amount >= 0) requireContext().getColor(R.color.income_color)
                else requireContext().getColor(R.color.expense_color)
            )
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addEditTransactionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
