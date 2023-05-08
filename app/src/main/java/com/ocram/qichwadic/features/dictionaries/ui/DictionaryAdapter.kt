package com.ocram.qichwadic.features.dictionaries.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.databinding.ItemDictionaryBinding

class DictionaryAdapter(private var dictionaries: List<DictionaryModel>, private val listener: DefinitionDownloadListener)
    : ListAdapter<DictionaryModel, DictionaryAdapter.DictionaryViewHolder>(DictionaryDiffCallback) {

    internal companion object {
        var totalEntries: String = ""
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDictionaries(dictionaries: List<DictionaryModel>) {
        this.dictionaries = dictionaries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val binding = ItemDictionaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        totalEntries = parent.context.getString(R.string.dictionary_totalEntries)
        val viewHolder = DictionaryViewHolder(binding)
        binding.ivDicAction.setOnClickListener {
            val pos = viewHolder.adapterPosition
            val dictionary = dictionaries[pos]
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
        holder.bindDictionary(dictionaries[position])
    }

    override fun getItemCount(): Int {
        return dictionaries.size
    }

    class DictionaryViewHolder(val binding: ItemDictionaryBinding) : RecyclerView.ViewHolder(binding.root) {

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

private object DictionaryDiffCallback : DiffUtil.ItemCallback<DictionaryModel>() {

    override fun areItemsTheSame(oldItem: DictionaryModel, newItem: DictionaryModel) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DictionaryModel, newItem: DictionaryModel) = oldItem == newItem
}