package com.ocram.qichwadic.features.dictionaries.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel

import kotlinx.android.synthetic.main.item_dictionary.view.*

class DictionaryAdapter(var dictionaries: List<DictionaryModel>?, private val listener: DefinitionDownloadListener) : RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {

    internal companion object {
        var totalEntries: String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        totalEntries = parent.context.getString(R.string.dictionary_totalEntries)
        val v = inflater.inflate(R.layout.item_dictionary, parent, false)
        val viewHolder = DictionaryViewHolder(v)
        v.ivDicAction.setOnClickListener {
            val pos = viewHolder.adapterPosition
            val dictionary = dictionaries!![pos]
            dictionary.downloading = true
            notifyItemChanged(pos)
            if (dictionary.existsInLocal) {
                listener.removeDictionary(pos, dictionary)
            } else {
                listener.downloadDefinitions(pos, dictionary)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        holder.bindDictionary(dictionaries!![position])
    }

    override fun getItemCount(): Int {
        return if (dictionaries != null) dictionaries!!.size else 0
    }

    class DictionaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindDictionary(dictionary: DictionaryModel) {
            itemView.tvDictionaryName.text = dictionary.name
            itemView.tvDictionaryAuthor.text = dictionary.author
            itemView.tvDictionaryDescription.text = dictionary.description
            itemView.tvDictionaryTotalEntries.text = String.format(totalEntries, dictionary.totalEntries)
            itemView.ivDicAction.setImageResource(if (dictionary.existsInLocal) R.drawable.ic_delete else R.drawable.ic_action_download)
            if (dictionary.downloading) {
                itemView.ivDicAction.visibility = View.GONE
                itemView.progress_bar.visibility = View.VISIBLE
            } else {
                itemView.ivDicAction.visibility = View.VISIBLE
                itemView.progress_bar.visibility = View.GONE
            }
        }
    }

    interface DefinitionDownloadListener {
        fun downloadDefinitions(pos: Int, dictionary: DictionaryModel)
        fun removeDictionary(pos: Int, dictionary: DictionaryModel)
    }

}
