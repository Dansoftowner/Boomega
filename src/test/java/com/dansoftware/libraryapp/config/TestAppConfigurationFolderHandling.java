package com.dansoftware.libraryapp.config;

import org.junit.Test;


public class TestAppConfigurationFolderHandling {


    @Test
    public void testConfigurationFolderRoot() {
        ConfigurationFolderFactory configurationFolderFactory = new ConfigurationFolderFactory();
        ConfigurationFolder configurationFolder = configurationFolderFactory.getConfigurationFolder();
    }

}
