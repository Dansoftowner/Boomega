package com.dansoftware.libraryapp.main;

/**
 * A BuildInfo contains every information about the
 * current build of the application.
 *
 * @see Globals#BUILD_INFO
 */
public class BuildInfo {

    private String version = "0.0.0";
    private String buildDate = "";

    BuildInfo() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }
}
