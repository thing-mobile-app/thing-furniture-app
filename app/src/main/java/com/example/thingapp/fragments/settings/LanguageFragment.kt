package com.example.thingapp.fragments.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.thingapp.databinding.FragmentLanguageBinding

class LanguageFragment : Fragment() {
    private lateinit var binding: FragmentLanguageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseLanguage.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.linearEnglish.setOnClickListener {
            setLanguage("en")
        }

        binding.linearTurkish.setOnClickListener {
            setLanguage("tr")
        }
    }

    private fun setLanguage(language: String) {
        val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (currentLocale == language) return

        val appLocale = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)

        val intent = requireActivity().intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val options = android.app.ActivityOptions
            .makeCustomAnimation(requireContext(), 0, 0)

        startActivity(intent, options.toBundle())
        requireActivity().finish()
    }
}
