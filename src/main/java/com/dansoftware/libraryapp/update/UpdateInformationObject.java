package com.dansoftware.libraryapp.update;

import java.util.Map;

/**
 * This class responsible for storing the information about
 * the new update
 * <p>
 * To fill this type of object with real information
 * you should use the {@link UpdateInformationObjectFactory}
 *
 * @see UpdateInformationObjectFactory#getInformation
 */
public class UpdateInformationObject {

    private String version;
    private String updateReviewHtmlPath;
    private Map<String, String> downloadableBinaries;

    /**
     * This constructor creates an UpdateInformationObject with the required values
     *
     * @param version              the new version of the update
     * @param updateReviewHtmlPath the location of the review webpage that describes
     *                             the features of the new update (http://example.com/libraryappreview.html).
     * @param downloadableBinaries the Map that contains the binary types and the location
     *                             of each downloadable binary on the web.
     *                             For example (if the app runs on windows) :<br/>
     * <pre>
     * {
     * "Exe installer" : "http://example.com/download/libraryapp-installer.exe", <br/>
     * "Zip archive" :  "http://example.com/download/libraryapp.zip" <br/>
     * }
     * </pre>
     */
    UpdateInformationObject(String version, String updateReviewHtmlPath, Map<String, String> downloadableBinaries) {
        this.version = version;
        this.downloadableBinaries = downloadableBinaries;
        this.updateReviewHtmlPath = updateReviewHtmlPath;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getDownloadableBinaries() {
        return downloadableBinaries;
    }

    public String getUpdateReviewHtmlPath() {
        return updateReviewHtmlPath;
    }
}
