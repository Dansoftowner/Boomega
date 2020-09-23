package com.dansoftware.libraryapp.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public final class SystemBrowser {
    public SystemBrowser() {
    }

    public void browse(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
