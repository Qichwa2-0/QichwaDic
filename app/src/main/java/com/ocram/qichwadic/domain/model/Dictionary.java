package com.ocram.qichwadic.domain.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;

import java.util.Objects;

@Entity(tableName = "dictionary")
public class Dictionary {

    @PrimaryKey
    @Expose
    private int id;

    @ColumnInfo
    @Expose
    private String name;

    @ColumnInfo
    @Expose
    private String author;

    @ColumnInfo
    @Expose
    private String description;

    @ColumnInfo(name = "language_begin")
    @Expose
    private String languageBegin;

    @ColumnInfo(name = "language_end")
    @Expose
    private String languageEnd;

    @ColumnInfo(name = "is_quechua")
    @Expose
    private boolean quechua;

    @ColumnInfo(name = "total_entries")
    @Expose
    private int totalEntries;

    @Ignore
    @Expose
    private String entriesFile;

    @Ignore
    private transient boolean existsInLocal;

    @Ignore
    private transient boolean isDownloading;

    public Dictionary() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguageBegin() {
        return languageBegin;
    }

    public void setLanguageBegin(String languageBegin) {
        this.languageBegin = languageBegin;
    }

    public String getLanguageEnd() {
        return languageEnd;
    }

    public void setLanguageEnd(String languageEnd) {
        this.languageEnd = languageEnd;
    }

    public boolean isQuechua() {
        return quechua;
    }

    public void setQuechua(boolean quechua) {
        this.quechua = quechua;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }

    public String getEntriesFile() {
        return entriesFile;
    }

    public void setEntriesFile(String entriesFile) {
        this.entriesFile = entriesFile;
    }

    public boolean isExistsInLocal() {
        return existsInLocal;
    }

    public void setExistsInLocal(boolean existsInLocal) {
        this.existsInLocal = existsInLocal;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dictionary that = (Dictionary) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
