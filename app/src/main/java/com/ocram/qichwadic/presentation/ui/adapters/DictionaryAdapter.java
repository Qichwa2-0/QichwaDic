package com.ocram.qichwadic.presentation.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ocram.qichwadic.R;
import com.ocram.qichwadic.domain.model.Dictionary;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>{

    private List<Dictionary> dictionaries;
    private final DefinitionDownloadListener listener;
    private static String totalEntries;

    public DictionaryAdapter(Context context, List<Dictionary> dictionaries, DefinitionDownloadListener listener) {
        this.dictionaries = dictionaries;
        this.listener = listener;
        totalEntries = context.getString(R.string.dictionary_totalEntries);
    }

    public void setDictionaries(List<Dictionary> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public List<Dictionary> getDictionaries() {
        return dictionaries;
    }

    @NonNull
    @Override
    public DictionaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_dictionary, parent, false);
        final DictionaryViewHolder viewHolder = new DictionaryViewHolder(v);
        viewHolder.ivDicAction.setOnClickListener(view -> {
            int pos = viewHolder.getAdapterPosition();
            Dictionary dictionary = dictionaries.get(pos);
            dictionary.setDownloading(true);
            notifyItemChanged(pos);
            if(dictionary.isExistsInLocal()){
                listener.removeDictionary(pos, dictionary);
            }else{
                listener.downloadDefinitions(pos, dictionary);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DictionaryViewHolder holder, int position) {
        holder.bindDictionary(dictionaries.get(position));
    }

    @Override
    public int getItemCount() {
        return dictionaries != null ? dictionaries.size() : 0;
    }

    static class DictionaryViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvDictionaryName) TextView tvDictionaryName;
        @BindView(R.id.tvDictionaryAuthor) TextView tvDictionaryAuthor;
        @BindView(R.id.tvDictionaryDescription) TextView tvDictionaryDescription;
        @BindView(R.id.tvDictionaryTotalEntries) TextView tvDictionaryTotalEntries;
        @BindView(R.id.ivDicAction) ImageView ivDicAction;
        @BindView(R.id.progress_bar) ProgressBar progress_bar;

        DictionaryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindDictionary(Dictionary dictionary){
            tvDictionaryName.setText(dictionary.getName());
            tvDictionaryAuthor.setText(dictionary.getAuthor());
            tvDictionaryDescription.setText(dictionary.getDescription());
            tvDictionaryTotalEntries.setText(String.format(totalEntries, dictionary.getTotalEntries()));
            if(dictionary.isDownloading()){
                ivDicAction.setVisibility(View.GONE);
                progress_bar.setVisibility(View.VISIBLE);
            }else{
                ivDicAction.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
            }
            ivDicAction.setImageResource(dictionary.isExistsInLocal() ? R.drawable.ic_delete : R.drawable.ic_action_download);
        }
    }

    public interface DefinitionDownloadListener{
        void downloadDefinitions(int pos, Dictionary dictionary);
        void removeDictionary(int pos, Dictionary dictionary);
    }
}
