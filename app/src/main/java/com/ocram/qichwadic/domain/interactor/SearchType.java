package com.ocram.qichwadic.domain.interactor;

public enum SearchType {

    CONTAINS(0, "%%%s%%"),
    EXACT(1, ""),
    STARTS_WITH(2, "%s%%"),
    ENDS_WITH(3, "%%%s");

    private int type;
    private String wordFormat;

    SearchType(int type, String wordFormat) {
        this.type = type;
        this.wordFormat = wordFormat;
    }

    public int getType() {
        return type;
    }

    public static String buildSearchCriteria(int type, String entry) {
        for(SearchType searchType : SearchType.values()) {
            if(type == EXACT.type) return entry;

            if(type == searchType.type) {
                return String.format(searchType.wordFormat, entry);
            }
        }
        return entry;
    }
}