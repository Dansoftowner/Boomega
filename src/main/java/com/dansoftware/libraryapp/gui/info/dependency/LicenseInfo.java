package com.dansoftware.libraryapp.gui.info.dependency;

public class LicenseInfo {

    private final String name;
    private final String websiteUrl;

    public LicenseInfo(String name, String websiteUrl) {
        this.name = name;
        this.websiteUrl = websiteUrl;
    }

    public String getName() {
        return name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }
}
