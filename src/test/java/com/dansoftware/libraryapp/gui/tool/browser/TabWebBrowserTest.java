package com.dansoftware.libraryapp.gui.tool.browser;

import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Locale;

public class TabWebBrowserTest extends Application {
    static {
        Theme.setDefault(Theme.DARK);
        Locale.setDefault(Locale.ENGLISH);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button dark = new Button("Dark");
        dark.setOnAction(e -> Theme.DARK.apply(primaryStage.getScene()));

        Button light = new Button("Light");
        light.setOnAction(e -> Theme.LIGHT.apply(primaryStage.getScene()));

        TabWebBrowser webBrowser = new TabWebBrowser(WebViewWrapper.WEBVIEW_SUPPLIER);
        webBrowser.load("Google pageee", "http://www.google.com");
        webBrowser.load("http://www.google.com");

        Scene scene = new Scene(new BorderPane(webBrowser,
                new HBox(10, dark, light),
                null,
                null,
                null));
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
