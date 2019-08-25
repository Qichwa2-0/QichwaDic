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
import com.ocram.qichwadic.domain.model.Definition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ResultViewHolder>{

    private List<Definition> definitions;
    private DefinitionClickListener listener;

    public ResultListAdapter(List<Definition> definitions, DefinitionClickListener listener){
        this.definitions = definitions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_list_result, parent, false);
        final ResultViewHolder viewHolder = new ResultViewHolder(v);
        viewHolder.ivShare.setOnClickListener(v1 ->
                listener.onItemShareClick(definitions.get(viewHolder.getAdapterPosition())));
        viewHolder.ivFavorite.setOnClickListener(v2 ->
                listener.onItemFavoriteClick(definitions.get(viewHolder.getAdapterPosition())));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
            Definition currentDefinition = definitions.get(position);
            holder.bindDefinition(currentDefinition);
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    public void pushMoreDefinitions(List<Definition> definitions){
        this.definitions.addAll(definitions);
    }

    @Override
    public int getItemCount() {
        return definitions != null ? definitions.size() : 0;
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvWord) TextView tvWord;
        @BindView(R.id.tvMeaning) TextView tvMeaning;
        @BindView(R.id.ivShare) ImageView ivShare;
        @BindView(R.id.ivFavorite) ImageView ivFavorite;

        ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindDefinition(final Definition definition){
            tvWord.setText(definition.getWord());
            tvMeaning.setText(HtmlCompat.fromHtml(definition.getMeaning(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }

    public interface DefinitionClickListener{
        void onItemShareClick(Definition definition);
        void onItemFavoriteClick(Definition definition);
    }
}
