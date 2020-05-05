package com.dansoftware.libraryapp.gui.browser;

import com.dansoftware.libraryapp.gui.util.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class TabWebBrowserTest extends Application {
    static {
        Theme.setDefault(Theme.LIGHT);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TabWebBrowser webBrowser = new TabWebBrowser(BrowserComponent.FX_WEBVIEW_SUPPLIER);
        webBrowser.load("Google pageee", "http://www.google.com");
        webBrowser.load("http://www.google.com");
        webBrowser.load("Local page", new File("test.html"));

        Scene scene = new Scene(webBrowser);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
