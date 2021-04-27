package com.ocram.qichwadic.features.help.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ocram.qichwadic.R

class SearchHelpFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentProviders: MutableList<SearchHelpFragmentProvider> = mutableListOf()

    init {
        fragmentProviders.add(
                SearchHelpFragmentProvider(fragment.getString(R.string.search_help_writing)) {
                    SearchHelpWritingFragment()
                }
        )
        fragmentProviders.add(
                SearchHelpFragmentProvider(fragment.getString(R.string.search_help_faq)) {
                    SearchHelpFaqFragment()
                }
        )
    }

    override fun getItemCount(): Int = fragmentProviders.size

    override fun createFragment(position: Int): Fragment {
        return fragmentProviders[position].createFragment()
    }

    fun getTitle(position: Int): String {
        return fragmentProviders[position].title
    }
}

private data class SearchHelpFragmentProvider(val title: String, val createFragment: () -> Fragment)