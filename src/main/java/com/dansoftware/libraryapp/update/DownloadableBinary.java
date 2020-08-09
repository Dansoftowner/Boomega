package com.dansoftware.libraryapp.update;

public class DownloadableBinary {

    private final String name;
    private final String fileExtension;
    private final String downloadUrl;

    public DownloadableBinary(String name, String fileExtension, String downloadUrl) {
        this.name = name;
        this.fileExtension = fileExtension;
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
