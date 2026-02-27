package com.financemanager

import android.app.Application
import com.financemanager.utils.PreferencesManager

class FinanceApp : Application() {
    val preferencesManager by lazy { PreferencesManager(this) }
}
