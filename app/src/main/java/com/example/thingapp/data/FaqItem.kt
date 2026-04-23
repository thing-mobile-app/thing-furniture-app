package com.example.thingapp.data

/**
 * Represents a single FAQ entry displayed in [com.example.thingapp.fragments.settings.HelpFragment].
 *
 * Instances are built at runtime from localised string resources so the text
 * automatically adapts when the user switches between English and Turkish.
 *
 * @property question The question text, already resolved from a string resource.
 * @property answer   The answer text, already resolved from a string resource.
 * @property isExpanded Controls whether the answer section is currently visible.
 *   Defaults to `false` (collapsed). [com.example.thingapp.adapters.FaqAdapter]
 *   mutates this flag directly when the user taps a row.
 */
data class FaqItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)
