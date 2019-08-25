package com.ocram.qichwadic.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(
		tableName = "definition",
		foreignKeys = {
				@ForeignKey(
						entity = Dictionary.class,
						parentColumns = "id",
						childColumns = "dictionary_id", onDelete = ForeignKey.CASCADE
				)
		},
		indices = {@Index("dictionary_id")}
)
public class Definition implements Serializable{

	@PrimaryKey(autoGenerate = true)
	private int id;

	@Expose
	@SerializedName("word")
	@ColumnInfo(name = "word")
	private String word;

	@Expose
	@SerializedName("meaning")
	@ColumnInfo(name = "meaning")
	private String meaning;

	@Expose
	@SerializedName("summary")
	@ColumnInfo(name = "summary")
	private String summary;

	@Expose
	@SerializedName("dictionaryName")
	@ColumnInfo(name = "dictionary_name")
	private String dictionaryName;

	@Expose
	@ColumnInfo(name = "dictionary_id")
	@SerializedName("dictionaryId")
	private int dictionaryId;

	public Definition() {
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
