package com.ocram.qichwadic.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "favorite")
public class Favorite implements Serializable {

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("word")
    @ColumnInfo(name = "word")
    private String word;

    @SerializedName("meaning")
    @ColumnInfo(name = "meaning")
    private String meaning;

    @SerializedName("summary")
    @ColumnInfo(name = "summary")
    private String summary;

    @SerializedName("dictionaryName")
    @ColumnInfo(name = "dictionary_name")
    private String dictionaryName;

    @SerializedName("dictionaryId")
    @ColumnInfo(name = "dictionary_id")
    private int dictionaryId;

    public Favorite() {
    }

    public Favorite(Definition definition) {
        this.word = definition.getWord();
        this.meaning = definition. getMeaning();
        this.summary = definition.getSummary();
        this.dictionaryName = definition.getDictionaryName();
        this.dictionaryId = definition.getDictionaryId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public int getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(int dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

