package com.dansoftware.libraryapp.util;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * SystemBrowser is a utility for communicating with the default browser of the system.
 *
 * @author Daniel Gyorffy
 */
public final class SystemBrowser {
    public SystemBrowser() {
    }

    public static boolean isSupported() {
        return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    }

    public void browse(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
