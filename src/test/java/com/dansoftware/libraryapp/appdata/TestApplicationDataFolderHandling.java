package com.dansoftware.libraryapp.appdata;

import org.junit.Test;


public class TestApplicationDataFolderHandling {


    @Test
    public void testConfigurationFolderRoot() {
        ApplicationDataFolderFactory applicationDataFolderFactory = new ApplicationDataFolderFactory();
        ApplicationDataFolder applicationDataFolder = applicationDataFolderFactory.getApplicationDataFolder();
    }

}
