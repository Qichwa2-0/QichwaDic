package com.ocram.qichwadic.presentation.viewmodel.viewstate;

public class SearchViewState {

    private boolean isLoading;
    private boolean hasError;
    private String message;

    public SearchViewState() {
    }

    public SearchViewState(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

}
