package com.dansoftware.libraryapp.update;

import java.util.Map;

/**
 * This class responsible for storing the information about
 * an update
 */
public class UpdateInformation {

    private String version;
    private String review;
    private Map<String, Map<String, String>> binaries;

    /**
     * This constructor creates an UpdateInformationObject with the required values
     *
     * @param version              the new version of the update
     * @param review the location of the review web page that describes
     *                             the features of the new update (http://example.com/libraryappreview.html).
     * @param binaries the Map that contains the binary types and the location
     *                             of each downloadable binary on the web.
     */
    public UpdateInformation(String version, String review, Map<String, Map<String, String>> binaries) {
        this.version = version;
        this.review = review;
        this.binaries = binaries;
    }

    public String getVersion() {
        return version;
    }

    public String getReview() {
        return review;
    }

    public Map<String, Map<String, String>> getBinaries() {
        return binaries;
    }
}
