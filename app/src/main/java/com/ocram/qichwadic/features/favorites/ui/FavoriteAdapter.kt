package com.ocram.qichwadic.features.favorites.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.databinding.ItemFavoriteBinding

class FavoriteAdapter(private var favorites: List<DefinitionModel>, private val listener: FavoriteClickListener) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = FavoriteViewHolder(binding)
        binding.ivShare.setOnClickListener { favorites[viewHolder.adapterPosition].let { it1 -> listener.onItemShare(it1) } }
        binding.ivRemove.setOnClickListener { favorites[viewHolder.adapterPosition].let { it1 -> listener.onItemRemove(it1) } }
        return viewHolder
    }


    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentDefinition = favorites[position]
        holder.bindDefinition(currentDefinition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFavorites(favorites: List<DefinitionModel>) {
        this.favorites = favorites
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    class FavoriteViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindDefinition(favorite: DefinitionModel) {
            binding.tvWord.text = favorite.word
            binding.tvMeaning.text = HtmlCompat.fromHtml(favorite.meaning!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvDicName.text = favorite.dictionaryName
        }

    }

    interface FavoriteClickListener {
        fun onItemShare(definition: DefinitionModel)
        fun onItemRemove(definition: DefinitionModel)
    }
}
