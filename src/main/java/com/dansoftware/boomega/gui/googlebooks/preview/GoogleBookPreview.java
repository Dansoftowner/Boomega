package com.dansoftware.boomega.gui.googlebooks.preview;

import com.dansoftware.boomega.service.googlebooks.Volume;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Used for showing a Google Book's preview content.
 *
 * @author Daniel Gyorffy
 */
public class GoogleBookPreview extends StackPane implements ContextTransformable {

    private final Volume volume;

    private WebView webView;
    private WebEngine webEngine;

    public GoogleBookPreview(@NotNull Volume volume) {
        this.volume = Objects.requireNonNull(volume);
        this.webView = new WebView();
        this.webEngine = webView.getEngine();
        this.buildUI();
        this.loadContent();
    }

    private void buildUI() {
        this.getChildren().add(webView);
    }

    private void loadContent() {
        this.webEngine.loadContent(HTMLContentGeneration.generateHTMLContent(volume), "text/html");
    }

    void clean() {
        this.webEngine.load("about:blank");
        java.net.CookieHandler.setDefault(new java.net.CookieManager());
        this.getChildren().clear();
        this.webView = null;
        this.webEngine = null;
    }

    @Override
    public @NotNull Context getContext() {
        return Context.empty();
    }
}
