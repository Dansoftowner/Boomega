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

    public static LicenseInfo apache20License() {
        return new LicenseInfo("Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0");
    }

    public static LicenseInfo wtfplLicense() {
        return new LicenseInfo("WTFPL License", "http://www.wtfpl.net/");
    }
}
