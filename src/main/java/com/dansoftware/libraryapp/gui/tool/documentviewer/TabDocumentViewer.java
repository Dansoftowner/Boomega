package com.dansoftware.libraryapp.gui.tool.documentviewer;

import com.dansoftware.libraryapp.util.DocumentOpener;
import com.sun.jdi.event.MethodEntryEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWord;
import static java.util.Objects.isNull;

/**
 * A TabDocumentViewer is a DocumentViewer that can open the documents and display
 * them in separate tabs.
 */
public class TabDocumentViewer extends DocumentViewer {

    private final TabPane tabPane;

    public TabDocumentViewer(Supplier<DocumentRenderer> rendererSupplier) {
        super(rendererSupplier);

        //creating the TabPane for the tabs
        this.tabPane = new TabPane();
        this.tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        this.getChildren().add(tabPane);
    }

    @Override
    public void load(String title, String url) throws IOException {
        Objects.requireNonNull(url, "The url can't be null");

        Tab tab = new Tab();

        if (isNull(title)) tab.setText(url);
        else tab.setText(title);

        DocumentRenderer documentRenderer = Objects.requireNonNull(getDocumentRendererSupplier().get(), "The DocumentRenderer supplier shouldn't return null!");
        documentRenderer.load(url);

        tab.setContent(documentRenderer.toNode());

        this.tabPane.getTabs().add(tab);
    }

    @Override
    public void load(String title, InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "The inputStream can't be null");

        Tab tab = new Tab();

        if (isNull(title)) tab.setText(getGeneralWord("document.viewer.tab.untitled"));
        else tab.setText(title);

        DocumentRenderer documentRenderer = Objects.requireNonNull(getDocumentRendererSupplier().get(), "The DocumentRenderer supplier shouldn't return null!");
        documentRenderer.load(inputStream);

        tab.setContent(documentRenderer.toNode());

        this.tabPane.getTabs().add(tab);
    }

    @Override
    public void load(String title, File file) throws IOException {
        Objects.requireNonNull(file, "The file can't be null");

        Tab tab = new Tab();

        /*
        MenuItem openInDefaultViewerItem = new MenuItem("Open in default viewer");
        openInDefaultViewerItem.setOnAction(e -> DocumentOpener.getOpener().open(file));

        tab.setContextMenu(new ContextMenu(openInDefaultViewerItem));

         */
        if (isNull(title)) tab.setText(file.getName());
        else tab.setText(title);

        DocumentRenderer documentRenderer = Objects.requireNonNull(getDocumentRendererSupplier().get(), "The DocumentRenderer supplier shouldn't return null!");
        documentRenderer.load(file);

        tab.setContent(documentRenderer.toNode());
        this.tabPane.getTabs().add(tab);
    }

    @Override
    public void load(String title, URL url) throws IOException {
        Objects.requireNonNull(url, "The url can't be null");

        Tab tab = new Tab();

        if (isNull(title)) tab.setText(url.toString());
        else tab.setText(title);

        DocumentRenderer documentRenderer = Objects.requireNonNull(getDocumentRendererSupplier().get(), "The DocumentRenderer supplier shouldn't return null!");
        documentRenderer.load(url);

        tab.setContent(documentRenderer.toNode());
        this.tabPane.getTabs().add(tab);
    }


}
