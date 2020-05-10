package com.dansoftware.libraryapp.gui.tool.browser;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.util.Objects;
import java.util.function.Supplier;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWord;

/**
 * A TabWebBrowser can load and show web pages in separate tabs
 *
 * <p>
 *  A TabWebBrowser is a {@link Browser} that creates a new tab for each
 *  web page on every load request.
 *
 * @author Daniel Gyorffy
 */
public class TabWebBrowser extends Browser {

    private final TabPane tabPane;

    public TabWebBrowser(Supplier<WebContentRenderer> browserComponentSupplier) {
        super(browserComponentSupplier);

        //creating the TabPane for the tabs
        this.tabPane = new TabPane();
        this.tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        this.tabPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.getChildren().add(tabPane);

        //if all tabs is closed the place holder will appears
        BrowserPlaceHolderGUI placeHolder = new BrowserPlaceHolderGUI();
        BooleanBinding isEmptyProperty = Bindings.isEmpty(tabPane.getTabs());
        placeHolder.visibleProperty().bind(isEmptyProperty);

        this.getChildren().add(placeHolder);
    }

    @Override
    public void load(String title, String url) {
        Objects.requireNonNull(url, "The url can't be null");

        Tab tab = new Tab();

        MenuItem pageDuplicatorMenuItem = new MenuItem(
                getGeneralWord("browser.tab.duplicate")
        );
        pageDuplicatorMenuItem.setOnAction(event -> load(title, url));

        tab.setContextMenu(new ContextMenu(pageDuplicatorMenuItem));


        WebContentRenderer webContentRenderer = Objects.requireNonNull(getWebRendererSupplier().get(), "The WebRendererSupplier shouldn't return null!");
        webContentRenderer.load(url);

        if (title == null) webContentRenderer.onTitleChanged(tab::setText);
        else tab.setText(title);

        tab.setContent(new Entry(webContentRenderer));

        this.tabPane.getTabs().add(tab);
    }

    /**
     * Represents a node that displays a WebContentRenderer with a BrowserToolbar
     */
    private class Entry extends BorderPane {
        private BrowserToolBar toolBar;
        private WebContentRenderer webContentRenderer;

        private Entry(WebContentRenderer webContentRenderer) {
            this.webContentRenderer = Objects.requireNonNull(webContentRenderer, "The browserComponent shouldn't be null");
            this.toolBar = new BrowserToolBar(webContentRenderer);

            this.setTop(toolBar);
            this.setCenter(webContentRenderer.toNode());
        }
    }
}
