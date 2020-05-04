package com.dansoftware.libraryapp.gui.browser;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.Objects;

/**
 * A TabWebBrowser can load and show web pages in separate tabs
 *
 * <p>
 *  A TabWebBrowser creates a new tab for each
 *  webview on every load request.
 */
public class TabWebBrowser extends Browser {

    private TabPane tabPane;

    public TabWebBrowser() {
        this.tabPane = new TabPane();
        this.tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        this.getChildren().add(tabPane);
    }

    @Override
    public void load(String title, String url) {
        Objects.requireNonNull(url, "The url can't be null"::toString);

        Tab tab = new Tab();
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load(url);

        if (title == null) tab.textProperty().bind(engine.titleProperty());
        else tab.textProperty().set(title);

        tab.setContent(new Entry(webView));

        this.tabPane.getTabs().add(tab);
    }

    private class Entry extends BorderPane {
        private BrowserToolBar toolBar;
        private WebView webView;

        public Entry(WebView webView) {
            this.webView = Objects.requireNonNull(webView, "The webView shouldn't be null"::toString);
            this.toolBar = new BrowserToolBar(webView);

            this.setTop(this.toolBar);
            this.setCenter(this.webView);
        }
    }
}
