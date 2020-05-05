package com.dansoftware.libraryapp.gui.browser;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A TabWebBrowser can load and show web pages in separate tabs
 *
 * <p>
 *  A TabWebBrowser creates a new tab for each
 *  webview on every load request.
 */
public class TabWebBrowser extends Browser {

    private TabPane tabPane;
    private Supplier<BrowserComponent> browserComponentSupplier;

    public TabWebBrowser(Supplier<BrowserComponent> browserComponentSupplier) {
        this.browserComponentSupplier = Objects.requireNonNull(browserComponentSupplier, "The supplier of BrowserComponent mustn't be null");

        this.tabPane = new TabPane();
        this.tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        this.getChildren().add(tabPane);

       // this.widthProperty().addListener((o, old, n) -> System.out.println("TabWebBrowser width: " + n));
    }

    @Override
    public void load(String title, String url) {
        Objects.requireNonNull(url, "The url can't be null");

        Tab tab = new Tab();
        BrowserComponent browserComponent = browserComponentSupplier.get();
        browserComponent.load(url);

        if (title == null) browserComponent.titleProperty().ifPresent(tab.textProperty()::bind);
        else tab.textProperty().set(title);

        tab.setContent(new Entry(browserComponent));

        this.tabPane.getTabs().add(tab);
    }

    private class Entry extends BorderPane {
        private BrowserToolBar toolBar;
        private BrowserComponent browserComponent;

        public Entry(BrowserComponent browserComponent) {
            this.browserComponent = Objects.requireNonNull(browserComponent, "The browserComponent shouldn't be null");
            this.toolBar = new BrowserToolBar(browserComponent);

            this.setTop(toolBar);
            this.setCenter(browserComponent.toNode());
        }
    }
}
