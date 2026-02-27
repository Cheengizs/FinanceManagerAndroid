package com.financemanager.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionCategory { INCOME, EXPENSE }

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val amount: Double,
    val date: String,
    val category: TransactionCategory
)
