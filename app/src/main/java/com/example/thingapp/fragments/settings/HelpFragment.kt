package com.example.thingapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thingapp.R
import com.example.thingapp.adapters.FaqAdapter
import com.example.thingapp.data.FaqItem
import com.example.thingapp.databinding.FragmentHelpBinding

/**
 * Displays the Help screen, which contains a static list of Frequently Asked Questions
 * relevant to the app's furniture-shopping features.
 *
 * The screen is opened from [ProfileFragment] via the Navigation Component.
 * A close button in the top-left navigates back to the previous destination.
 *
 * ### Architecture notes
 * No ViewModel is required because the FAQ content is purely static. Questions and
 * answers are resolved from string resources at runtime, so they are automatically
 * localised when the user switches between English and Turkish via the Language screen.
 */
class HelpFragment : Fragment() {

    /** View binding instance for `fragment_help.xml`. Initialised in [onCreateView]. */
    private lateinit var binding: FragmentHelpBinding

    /**
     * Inflates `fragment_help.xml` and returns its root view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called after the view hierarchy is created.
     *
     * Attaches the close-button listener and initialises the FAQ [RecyclerView][androidx.recyclerview.widget.RecyclerView].
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseHelp.setOnClickListener {
            findNavController().navigateUp()
        }

        setupFaqRecyclerView()
    }

    /**
     * Creates the [FaqAdapter] from [buildFaqList] and attaches it to [binding.rvFaq].
     *
     * `setHasFixedSize(false)` is used because the RecyclerView height depends on
     * the expanded/collapsed state of individual FAQ items.
     */
    private fun setupFaqRecyclerView() {
        val faqItems = buildFaqList()
        val adapter  = FaqAdapter(faqItems)

        binding.rvFaq.apply {
            this.adapter  = adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
    }

    /**
     * Builds and returns the ordered list of [FaqItem]s from localised string resources.
     *
     * Add or remove entries here to change what appears on the Help screen.
     * Each `faq_qN` / `faq_aN` pair must have a matching entry in both
     * `res/values/strings.xml` (English) and `res/values-tr/strings.xml` (Turkish).
     *
     * @return A [MutableList] of [FaqItem]s, all initially collapsed.
     */
    private fun buildFaqList(): MutableList<FaqItem> = mutableListOf(
        FaqItem(getString(R.string.faq_q1), getString(R.string.faq_a1)),
        FaqItem(getString(R.string.faq_q2), getString(R.string.faq_a2)),
        FaqItem(getString(R.string.faq_q3), getString(R.string.faq_a3)),
        FaqItem(getString(R.string.faq_q4), getString(R.string.faq_a4)),
        FaqItem(getString(R.string.faq_q6), getString(R.string.faq_a6)),
        FaqItem(getString(R.string.faq_q7), getString(R.string.faq_a7)),
        FaqItem(getString(R.string.faq_q8), getString(R.string.faq_a8))
    )
}
