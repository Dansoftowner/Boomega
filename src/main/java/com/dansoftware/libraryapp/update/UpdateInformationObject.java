package com.dansoftware.libraryapp.update;

/**
 * This class responsible for storing the information about
 * the update
 *
 * To fill this type of object with real information
 * you should use the {@link UpdateInformationObjectFactory}
 *
 * @see UpdateInformationObjectFactory#getInformation
 */
public class UpdateInformationObject {

    /**
     * Package-private constructor
     */
    UpdateInformationObject() {
    }

    private String version;
    private String downloadableInstallerPath;
    private String updateReviewHtmlPath;


    public UpdateInformationObject(String version, String downloadableInstallerPath, String updateReviewHtmlPath) {
        this.version = version;
        this.downloadableInstallerPath = downloadableInstallerPath;
        this.updateReviewHtmlPath = updateReviewHtmlPath;
    }

    public String getVersion() {
        return version;
    }

    public String getDownloadableInstallerPath() {
        return downloadableInstallerPath;
    }

    public String getUpdateReviewHtmlPath() {
        return updateReviewHtmlPath;
    }
}
