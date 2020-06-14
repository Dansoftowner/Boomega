package com.dansoftware.libraryapp.main;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class gives ability for another parts of the application to access some
 * global information/object
 */
public final class Globals {

    public static final Image WINDOW_ICON;
    static {
        Image temp = null;
        try(InputStream inputStream =
                    Globals.class.getResourceAsStream("/com/dansoftware/libraryapp/image/libraryapp-win-icon.png")) {
            temp = new Image(inputStream);
        } catch (IOException ignored) {
            //we don't care about that the input stream closed with throwing exception
        }

        WINDOW_ICON = temp;
    }

    public static final VersionInfo VERSION_INFO = new VersionInfo("0.0.0");


    /**
     * Don't let anyone to create an instance of this class
     */
    private Globals() {
    }

}
