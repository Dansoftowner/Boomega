package com.dansoftware.libraryapp.gui.info.dependency;

public class DependencyInfo {

    private final String name;
    private final String websiteUrl;
    private final LicenseInfo licenseInfo;

    public DependencyInfo(String name, String websiteUrl, LicenseInfo licenseInfo) {
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.licenseInfo = licenseInfo;
    }

    public String getName() {
        return name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public LicenseInfo getLicenseInfo() {
        return licenseInfo;
    }
}
