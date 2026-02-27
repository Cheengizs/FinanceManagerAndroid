package com.financemanager.data.db

import android.content.Context
import androidx.room.*

class Converters {
    @TypeConverter
    fun fromCategory(cat: TransactionCategory): String = cat.name
    @TypeConverter
    fun toCategory(value: String): TransactionCategory = TransactionCategory.valueOf(value)
}

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
