package com.dansoftware.libraryapp.main;

import javafx.scene.image.Image;

/**
 * This class gives ability for another parts of the application to access some
 * global information/object
 */
public final class Globals {

    public static final Image WINDOW_ICON = new Image(
            Globals.class.getResourceAsStream("/com/dansoftware/libraryapp/image/libraryapp-win-icon.png")
    );

    public static final VersionInfo VERSION_INFO = new VersionInfo("0.0.0");


    /**
     * Don't let anyone to create an instance of this class
     */
    private Globals() {
    }

}
