package com.ocram.qichwadic.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    @ColumnInfo(name = "dictionary_id")
    private int dictionaryId;

    @ColumnInfo(name = "dictionary_name")
    private String dictionaryName;

    @ColumnInfo(name = "total")
    private int total;

    @Ignore
    private List<Definition> definitions;

    public SearchResult() {
    }

    public SearchResult(Dictionary dictionary) {
        this.dictionaryId = dictionary.getId();
        this.dictionaryName = dictionary.getName();
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    public void addDefinition(Definition definition) {
        if(this.definitions == null) {
            this.definitions = new ArrayList<>();
        }
        this.definitions.add(definition);
    }

    public int getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(int dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
