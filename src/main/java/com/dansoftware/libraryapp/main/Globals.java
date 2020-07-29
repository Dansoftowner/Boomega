package com.dansoftware.libraryapp.main;

import javafx.scene.image.Image;

/**
 * This class gives ability for another parts of the application to access some
 * global information/object
 */
public final class Globals {

    /**
     * The libraryapp icon.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">The icon</a>
     */
    public static final Image ICON = new Image(
            Globals.class.getResourceAsStream("/com/dansoftware/libraryapp/image/win-icon.png")
    );

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
