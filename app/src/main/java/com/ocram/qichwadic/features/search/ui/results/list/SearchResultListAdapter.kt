package com.ocram.qichwadic.features.search.ui.results.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel

import kotlinx.android.synthetic.main.item_list_result.view.*

class SearchResultListAdapter(private var definitions: MutableList<DefinitionModel>?, private val listener: DefinitionClickListener) : RecyclerView.Adapter<SearchResultListAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_list_result, parent, false)
        val viewHolder = ResultViewHolder(v)
        v.ivShare.setOnClickListener { listener.onItemShareClick(definitions!![viewHolder.adapterPosition]) }
        v.ivFavorite.setOnClickListener { listener.onItemFavoriteClick(definitions!![viewHolder.adapterPosition]) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val currentDefinition = definitions!![position]
        holder.bindDefinition(currentDefinition)
    }

    fun setDefinitions(definitions: MutableList<DefinitionModel>) {
        this.definitions = definitions
    }

    fun pushMoreDefinitions(definitions: List<DefinitionModel>) {
        this.definitions?.addAll(definitions)
    }

    override fun getItemCount(): Int {
        return definitions?.size ?: 0
    }

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindDefinition(definition: DefinitionModel) {
            itemView.tvWord.text = definition.word
            itemView.tvMeaning.text = HtmlCompat.fromHtml(definition.meaning!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    interface DefinitionClickListener {
        fun onItemShareClick(definition: DefinitionModel)
        fun onItemFavoriteClick(definition: DefinitionModel)
    }
}
