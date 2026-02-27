package com.financemanager.viewmodel

import androidx.lifecycle.*
import com.financemanager.data.db.Transaction
import com.financemanager.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allTransactions: LiveData<List<Transaction>> = repository.allTransactions
    val balance: LiveData<Double?> = repository.balance

    fun insert(t: Transaction) = viewModelScope.launch { repository.insert(t) }
    fun update(t: Transaction) = viewModelScope.launch { repository.update(t) }
    fun delete(t: Transaction) = viewModelScope.launch { repository.delete(t) }
    suspend fun getById(id: Int): Transaction? = repository.getById(id)
}

class TransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
