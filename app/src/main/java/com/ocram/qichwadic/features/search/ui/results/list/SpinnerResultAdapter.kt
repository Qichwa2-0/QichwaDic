package com.ocram.qichwadic.features.search.ui.results.list

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.SearchResultModel

class SpinnerResultAdapter(context: Context, @LayoutRes resource: Int, private var searchResults: List<SearchResultModel>?) : ArrayAdapter<SearchResultModel>(context, resource) {

    fun setSearchResults(searchResults: List<SearchResultModel>) {
        this.searchResults = searchResults
    }

    override fun getCount(): Int {
        return this.searchResults!!.size
    }

    override fun getItem(position: Int): SearchResultModel? {
        return searchResults!![position]
    }

    @SuppressLint("SetTextI18n")
    private fun getCustomView(position: Int, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.item_spinner_result, parent, false)

        val currentResult = getItem(position)
        val tvName = layout.findViewById<TextView>(R.id.tvName)
        val tvResultsTotal = layout.findViewById<TextView>(R.id.tvResultsTotal)
        if (currentResult != null) {
            tvName.text = "${currentResult.dictionaryName}"
            tvResultsTotal.text = "(${currentResult.total})"
        }
        return layout
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }
}
