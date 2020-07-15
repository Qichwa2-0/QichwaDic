package com.ocram.qichwadic.features.favorites.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView

import com.ocram.qichwadic.R
import com.ocram.qichwadic.features.common.domain.DefinitionModel

import kotlinx.android.synthetic.main.item_favorite.view.*

class FavoriteAdapter(private var favorites: List<DefinitionModel>?, private val listener: FavoriteClickListener) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_favorite, parent, false)
        val viewHolder = FavoriteViewHolder(v)
        v.ivShare.setOnClickListener { favorites?.get(viewHolder.adapterPosition)?.let { it1 -> listener.onItemShare(it1) } }
        v.ivRemove.setOnClickListener { favorites?.get(viewHolder.adapterPosition)?.let { it1 -> listener.onItemRemove(it1) } }
        return viewHolder
    }


    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentDefinition = favorites!![position]
        holder.bindDefinition(currentDefinition)
    }

    fun setFavorites(favorites: List<DefinitionModel>) {
        this.favorites = favorites
    }

    override fun getItemCount(): Int {
        return favorites!!.size
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindDefinition(favorite: DefinitionModel) {
            itemView.tvWord.text = favorite.word
            itemView.tvMeaning.text = HtmlCompat.fromHtml(favorite.meaning!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
            itemView.tvDicName.text = favorite.dictionaryName
        }

    }

    interface FavoriteClickListener {
        fun onItemShare(definition: DefinitionModel)
        fun onItemRemove(definition: DefinitionModel)
    }
}
