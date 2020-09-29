package com.dansoftware.libraryapp.gui.info.dependency;

/**
 * A {@link LicenseInfo} represents a software-license and holds the information of it.
 *
 * @author Daniel Gyorffy
 */
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

    /**
     * Creates a {@link LicenseInfo} that represents a MIT License.
     *
     * @return the LicenseInfo object
     */
    public static LicenseInfo mitLicense() {
        return new LicenseInfo("MIT License", "https://opensource.org/licenses/MIT");
    }

    /**
     * Creates a {@link LicenseInfo} that represents an Apache 2.0 license.
     *
     * @return the LicenseInfo object
     */
    public static LicenseInfo apache20License() {
        return new LicenseInfo("Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0");
    }

    /**
     * Creates a {@link LicenseInfo} that represents a WTFPL license.
     *
     * @return the LicenseInfo object
     */
    public static LicenseInfo wtfplLicense() {
        return new LicenseInfo("WTFPL License", null);
    }
}
