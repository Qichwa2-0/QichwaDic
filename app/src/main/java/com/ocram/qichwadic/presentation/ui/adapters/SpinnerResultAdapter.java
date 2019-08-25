package com.ocram.qichwadic.presentation.ui.adapters;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ocram.qichwadic.R;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

public class SpinnerResultAdapter extends ArrayAdapter<SearchResult> {

    private List<SearchResult> searchResults;
    private Context context;

    public SpinnerResultAdapter(@NonNull Context context, @LayoutRes int resource, List<SearchResult> searchResults) {
        super(context, resource);
        this.context = context;
        this.searchResults = searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return this.searchResults.size();
    }

    @Nullable
    @Override
    public SearchResult getItem(int position) {
        return searchResults.get(position);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.item_spinner_result, parent, false);

        SearchResult currentResult = getItem(position);
        TextView tvName = layout.findViewById(R.id.tvName);
        if(currentResult != null){
            tvName.setText(currentResult.getDictionaryName());
        }
        return layout;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
