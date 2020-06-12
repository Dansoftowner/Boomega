package com.dansoftware.libraryapp.main;

import static com.dansoftware.libraryapp.main.Globals.VERSION_INFO;

/**
 * A BuildInfo contains every information about the
 * current build of the application.
 *
 * @see Globals#VERSION_INFO
 */
public class VersionInfo implements Comparable<VersionInfo> {

    private String version;

    public VersionInfo(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(VersionInfo other) {
        String thisVersion = this.getVersion().replace(".", "");
        String otherVersion = other.getVersion().replace(".", "");

        return Integer.parseInt(thisVersion) - Integer.parseInt(otherVersion);
    }
}
