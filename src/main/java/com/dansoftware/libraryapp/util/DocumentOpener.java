package com.dansoftware.libraryapp.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * A DocumentOpener can open files and web pages
 * with the OperatingSystem
 */
public class DocumentOpener {

    private static final DocumentOpener opener = new DocumentOpener();

    private DocumentOpener() {
    }

    public void open(File file) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void browse(String webLocation) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(URI.create(webLocation));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static DocumentOpener getOpener() {
        return opener;
    }
}
