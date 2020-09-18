package com.dansoftware.libraryapp.main;

/**
 * This class gives ability for another parts of the application to access some
 * global information/object
 */
@Deprecated
public final class Globals {

    /**
     * The libraryapp database file-extension
     */
    public static final String FILE_EXTENSION = "lbadb";

    /**
     * The current version-info object
     */
    public static final VersionInfo VERSION_INFO = new VersionInfo("0.0.0");

    /**
     * Don't let anyone to create an instance of this class
     */
    private Globals() {
    }

}
