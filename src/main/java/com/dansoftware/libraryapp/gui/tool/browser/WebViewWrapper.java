package com.dansoftware.libraryapp.gui.tool.browser;

import javafx.scene.Node;
import javafx.scene.web.WebView;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A WebViewWrapper wraps a javaFX WebView and
 * provides the functionality through the {@link WebContentRenderer}
 * kind of interface.
 *
 * @see WebView
 */
public class WebViewWrapper implements WebContentRenderer {

    /**
     * Defines a Supplier which returns a basic WebViewWrapper with a simple javaFX Webview
     */
    public static final Supplier<WebContentRenderer> WEBVIEW_SUPPLIER = () -> new WebViewWrapper(new WebView());


    private final WebView webView;

    /**
     * Creates a normal WebViewWrapper
     *
     * @param webView the WebView that should be wrapped
     */
    public WebViewWrapper(WebView webView) {
        this.webView = Objects.requireNonNull(webView, "The webView mustn't be null");
    }

    @Override
    public void goToPreviousPage() {
        this.webView.getEngine().executeScript("window.history.back();");
    }

    @Override
    public void goToForwardPage() {
        this.webView.getEngine().executeScript("window.history.forward();");
    }

    @Override
    public void reload() {
        webView.getEngine().reload();
    }

    @Override
    public void load(String url) {
        webView.getEngine().load(url);
    }

    @Override
    public void onLocationChanged(Consumer<String> locationConsumer) {
         webView.getEngine()
                .locationProperty()
                .addListener((observable, oldValue, newValue) ->
                        locationConsumer.accept(newValue)
                );
    }

    @Override
    public void onTitleChanged(Consumer<String> titleConsumer) {
         webView.getEngine()
                .titleProperty()
                .addListener((observable, oldValue, newValue) ->
                        titleConsumer.accept(newValue)
                );
    }

    @Override
    public String getLocation() {
        return webView.getEngine().getLocation();
    }

    @Override
    public Node toNode() {
        return webView;
    }
}
