package com.dansoftware.libraryapp.gui.info.dependency.meta;

import java.util.Optional;

/**
 * A {@link DependencyInfo} represents a software that is used by this application.
 *
 * @author Daniel Gyorffy
 */
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

    public Optional<String> getWebsiteUrl() {
        return Optional.ofNullable(websiteUrl);
    }

    public LicenseInfo getLicenseInfo() {
        return licenseInfo;
    }
}
