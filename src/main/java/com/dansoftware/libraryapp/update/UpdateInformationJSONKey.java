package com.dansoftware.libraryapp.update;

/**
 * This enum contains the keys for the JSON that contains update information
 */
public enum UpdateInformationJSONKey {
    VERSION("version"),
    REVIEW_PAGE("review"),
    DOWNLOADABLE_BINARIES("binaries");

    private String value;

    UpdateInformationJSONKey(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
