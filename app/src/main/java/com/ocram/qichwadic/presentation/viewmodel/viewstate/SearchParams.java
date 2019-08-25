package com.ocram.qichwadic.presentation.viewmodel.viewstate;

public class SearchParams {

    public static final int MAX_SEARCH_RESULTS = 20;

    private int searchTypePos;
    private boolean fromQuechua;
    private String nonQuechuaLangCode;

    public SearchParams(int searchTypePos, boolean fromQuechua, String nonQuechuaLangCode) {
        this.searchTypePos = searchTypePos;
        this.fromQuechua = fromQuechua;
        this.nonQuechuaLangCode = nonQuechuaLangCode;
    }

    public SearchParams() {
        this.searchTypePos = 2;
        this.fromQuechua = true;
        this.nonQuechuaLangCode = "es";
    }

    public int getSearchTypePos() {
        return searchTypePos;
    }

    public void setSearchTypePos(int searchTypePos) {
        this.searchTypePos = searchTypePos;
    }

    public boolean isFromQuechua() {
        return fromQuechua;
    }

    public void setFromQuechua(boolean fromQuechua) {
        this.fromQuechua = fromQuechua;
    }

    public String getNonQuechuaLangCode() {
        return nonQuechuaLangCode;
    }

    public void setNonQuechuaLangCode(String nonQuechuaLangCode) {
        this.nonQuechuaLangCode = nonQuechuaLangCode;
    }
}
