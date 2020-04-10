package com.dansoftware.libraryapp.appdata;

import com.sun.javafx.PlatformUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationDataFolderTest {

    @Test
    public void testApplicationDataFolderFactory() {
        assertDoesNotThrow(ApplicationDataFolderFactory::getApplicationDataFolder);
        assertNotNull(ApplicationDataFolderFactory.getApplicationDataFolder());
    }

    @Test
    public void testConfigurationFolderRoot() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        File rootConfigDirectory = applicationDataFolder.getRootApplicationDataDirectory();

        assertNotNull(rootConfigDirectory, "The root appdata directory shouldn't be null");

        File expected = null;
        if (PlatformUtil.isWindows())
            expected = new File(System.getenv("APPDATA"), "Dansoftware/libraryapp_2020");
        else if (PlatformUtil.isLinux())
           expected = new File(System.getProperty("user.home"), ".libraryapp2020/profiles/");
        else if (PlatformUtil.isMac())
            expected = new File("~/Library/Application Support", "Dansoftware/libraryapp_2020");

        assertEquals(expected.getAbsolutePath(), rootConfigDirectory.getAbsolutePath());
    }

    @Test
    public void testConfigurationFileExist() {

        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        File configFile = applicationDataFolder.getConfigurationFile();

        assertNotNull(configFile);
        assertTrue(configFile.exists());
    }

}
