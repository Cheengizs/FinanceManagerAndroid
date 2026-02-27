package com.financemanager.data.repository

import androidx.lifecycle.LiveData
import com.financemanager.data.db.Transaction
import com.financemanager.data.db.TransactionDao

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: LiveData<List<Transaction>> = dao.getAllTransactions()
    val balance: LiveData<Double?> = dao.getBalance()

    suspend fun insert(t: Transaction) = dao.insertTransaction(t)
    suspend fun update(t: Transaction) = dao.updateTransaction(t)
    suspend fun delete(t: Transaction) = dao.deleteTransaction(t)
    suspend fun getById(id: Int): Transaction? = dao.getTransactionById(id)
}
