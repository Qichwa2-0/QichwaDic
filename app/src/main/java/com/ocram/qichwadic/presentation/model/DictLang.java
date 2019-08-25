package com.ocram.qichwadic.presentation.model;

import androidx.annotation.NonNull;

public class DictLang {

    private static final String SPLIT_DELIMITER = ";";

    private String code;
    private String name;

    public DictLang(String codeName) {
        String[] codeNameSplit = codeName.split(SPLIT_DELIMITER);
        this.code = codeNameSplit[0];
        this.name = codeNameSplit[1];
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    public boolean isQuechua() {
        return "qu".equalsIgnoreCase(this.code);
    }
}
