package com.ocram.qichwadic.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ocram.qichwadic.R;
import com.ocram.qichwadic.domain.model.Favorite;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{

    private List<Favorite> favorites;
    private FavoriteClickListener listener;

    public FavoriteAdapter(List<Favorite> favorites, FavoriteClickListener listener){
        this.favorites = favorites;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_favorite, parent, false);
        final FavoriteViewHolder viewHolder = new FavoriteViewHolder(v);
        viewHolder.ivShare.setOnClickListener(v1 ->
                listener.onItemShare(favorites.get(viewHolder.getAdapterPosition())));
        viewHolder.ivRemove.setOnClickListener(v2 ->
                listener.onItemRemove(favorites.get(viewHolder.getAdapterPosition())));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
            Favorite currentDefinition = favorites.get(position);
            holder.bindDefinition(currentDefinition);
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvWord) TextView tvWord;
        @BindView(R.id.tvMeaning) TextView tvMeaning;
        @BindView(R.id.ivShare) ImageView ivShare;
        @BindView(R.id.ivRemove) ImageView ivRemove;
        @BindView(R.id.tvDicName) TextView tvDicName;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindDefinition(final Favorite favorite){
            tvWord.setText(favorite.getWord());
            tvMeaning.setText(HtmlCompat.fromHtml(favorite.getMeaning(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            tvDicName.setText(favorite.getDictionaryName());
        }

    }

    public interface FavoriteClickListener {
        void onItemShare(Favorite definition);
        void onItemRemove(Favorite definition);
    }
}
