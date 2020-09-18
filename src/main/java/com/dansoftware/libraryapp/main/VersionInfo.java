package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.util.adapter.VersionInteger;

/**
 * A BuildInfo contains every information about the
 * current build of the application.
 */
@Deprecated
public class VersionInfo implements Comparable<VersionInfo> {

    private String version;
    private String buildInfo;

    public VersionInfo(String version) {
        this.version = version;
    }

    public VersionInfo(String version, String buildInfo) {
        this.version = version;
        this.buildInfo = buildInfo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(VersionInfo other) {
        String thisVersion = this.getVersion();
        String otherVersion = other.getVersion();

        return new VersionInteger(thisVersion).getValue() - new VersionInteger(otherVersion).getValue();
    }

    @Override
    public String toString() {
        return this.version;
    }

    public String getBuildInfo() {
        return null;
    }
}
