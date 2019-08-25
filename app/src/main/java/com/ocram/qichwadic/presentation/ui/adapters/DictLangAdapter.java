package com.ocram.qichwadic.presentation.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ocram.qichwadic.R;
import com.ocram.qichwadic.presentation.model.DictLang;

import java.util.ArrayList;
import java.util.List;

public class DictLangAdapter extends ArrayAdapter<DictLang> {

    private List<DictLang> dictLangs;
    private Context context;

    public DictLangAdapter(@NonNull Context context) {
        super(context, R.layout.item_spinner_dict_lang);
        this.context = context;
        String[] codeTextLangs = context.getResources().getStringArray(R.array.dictLangs);
        this.dictLangs = new ArrayList<>();
        for(String codeTextLang : codeTextLangs) {
            DictLang dictLang = new DictLang(codeTextLang);
            if(!dictLang.isQuechua()) {
                this.dictLangs.add(dictLang);
            }
        }
    }

    @Override
    public int getCount() {
        return this.dictLangs.size();
    }

    public int getPosByValue(String langCode) {
        for(int i = 0; i < this.dictLangs.size() ; i++) {
            DictLang dictLang = this.dictLangs.get(i);
            if(dictLang.getCode().equalsIgnoreCase(langCode)) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    @Override
    public DictLang getItem(int position) {
        return dictLangs.get(position);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent, boolean dropdown) {
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutId = (dropdown) ? R.layout.item_spinner_dropdown_search_type : R.layout.item_spinner_dict_lang;
        View layout = inflater.inflate(layoutId, parent, false);

        DictLang dictLang = getItem(position);
        TextView tvName = layout.findViewById(R.id.tvName);
        if(dictLang != null){
            String displayText = (dropdown) ? dictLang.getName() : dictLang.getCode().toUpperCase();
            tvName.setText(displayText);
        }
        return layout;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }
}
