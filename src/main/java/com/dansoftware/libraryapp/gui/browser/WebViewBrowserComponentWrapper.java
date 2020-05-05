package com.dansoftware.libraryapp.gui.browser;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.web.WebView;

import java.util.Objects;
import java.util.Optional;

public class WebViewBrowserComponentWrapper implements BrowserComponent {

    private final WebView webView;

    public WebViewBrowserComponentWrapper(WebView webView) {
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
    public Optional<DoubleProperty> widthProperty() {
        return Optional.of(webView.prefWidthProperty()); }

    @Override
    public Optional<ObservableValue<String>> locationProperty() {
        return Optional.of(this.webView.getEngine().locationProperty());
    }

    @Override
    public Optional<ObservableValue<String>> titleProperty() {
        return Optional.of(webView.getEngine().titleProperty());
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
