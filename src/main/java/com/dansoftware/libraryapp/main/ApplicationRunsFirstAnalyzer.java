package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;

class ApplicationRunsFirstAnalyzer {


    public void analyze() {
        if (applicationRunsFirst()) {
            //do job
        }
    }

    private boolean applicationRunsFirst() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        return applicationDataFolder.isFirstCreated();
    }

}
