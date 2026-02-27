package com.financemanager.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.financemanager.FinanceApp
import com.financemanager.databinding.FragmentSettingsBinding
import com.financemanager.ui.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = (requireActivity().application as FinanceApp).preferencesManager

        // Load saved prefs
        lifecycleScope.launch {
            binding.switchTheme.isChecked = prefs.isDarkTheme.first()
            when (prefs.language.first()) {
                "ru" -> binding.rbRussian.isChecked = true
                else -> binding.rbEnglish.isChecked = true
            }
        }

        // Theme toggle
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                prefs.setDarkTheme(isChecked)
                (requireActivity() as MainActivity).recreate()
            }
        }

        // Language
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val lang = if (checkedId == binding.rbRussian.id) "ru" else "en"
            lifecycleScope.launch {
                prefs.setLanguage(lang)
                (requireActivity() as MainActivity).recreateWithLocale(lang)
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
