package com.dansoftware.libraryapp.gui.browser;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.web.WebView;

import java.util.Optional;
import java.util.function.Supplier;

public interface BrowserComponent {
    Supplier<BrowserComponent> FX_WEBVIEW_SUPPLIER = () -> new WebViewBrowserComponentWrapper(new WebView());

    void goToPreviousPage();
    void goToForwardPage();
    void reload();
    void load(String url);

    Optional<DoubleProperty> widthProperty();
    Optional<ObservableValue<String>> locationProperty();
    Optional<ObservableValue<String>> titleProperty();
    String getLocation();
    Node toNode();
}
