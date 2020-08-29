package com.dansoftware.libraryapp.update;

import java.util.List;

/**
 * This class responsible for storing the information about
 * an update
 */
public class UpdateInformation {

    private String version;
    private String reviewUrl;
    private List<DownloadableBinary> binaries;

    /**
     * This constructor creates an UpdateInformationObject with the required values
     *
     * @param version   the new version of the update
     * @param reviewUrl the location of the review web page that describes
     *                  the features of the new update (http://example.com/libraryappreview.html).
     * @param binaries  the Map that contains the binary types and the location
     *                  of each downloadable binary on the web.
     */
    public UpdateInformation(String version, String reviewUrl, List<DownloadableBinary> binaries) {
        this.version = version;
        this.reviewUrl = reviewUrl;
        this.binaries = binaries;
    }

    public String getVersion() {
        return version;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public List<DownloadableBinary> getBinaries() {
        return binaries;
    }
}
