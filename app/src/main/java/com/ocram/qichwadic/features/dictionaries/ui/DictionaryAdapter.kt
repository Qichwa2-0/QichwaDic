package com.ocram.qichwadic.features.dictionaries.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.databinding.ItemDictionaryBinding

class DictionaryAdapter(var dictionaries: List<DictionaryModel>?, private val listener: DefinitionDownloadListener) : RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {

    internal companion object {
        var totalEntries: String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        totalEntries = parent.context.getString(R.string.dictionary_totalEntries)
        val v = inflater.inflate(R.layout.item_dictionary, parent, false)
        val viewHolder = DictionaryViewHolder(v)

        val binding = ItemDictionaryBinding.bind(v)
        binding.ivDicAction.setOnClickListener {
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
        private val binding = ItemDictionaryBinding.bind(itemView)

        fun bindDictionary(dictionary: DictionaryModel) {
            binding.tvDictionaryName.text = dictionary.name
            binding.tvDictionaryAuthor.text = dictionary.author
            binding.tvDictionaryDescription.text = dictionary.description
            binding.tvDictionaryTotalEntries.text = String.format(totalEntries, dictionary.totalEntries)
            binding.ivDicAction.setImageResource(if (dictionary.existsInLocal) R.drawable.ic_delete else R.drawable.ic_action_download)
            if (dictionary.downloading) {
                binding.ivDicAction.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.ivDicAction.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    interface DefinitionDownloadListener {
        fun downloadDefinitions(pos: Int, dictionary: DictionaryModel)
        fun removeDictionary(pos: Int, dictionary: DictionaryModel)
    }

}
