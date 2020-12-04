package com.dansoftware.libraryapp.gui.tool.documentviewer;

import com.dansoftware.libraryapp.util.SystemBrowser;
import com.dansoftware.libraryapp.util.function.CaughtRunnable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.TransferMode;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

import static com.dansoftware.libraryapp.locale.I18N.getGeneralValue;
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
        this.tabPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.getChildren().add(tabPane);

        //set file-dragging functionality
        this.tabPane.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });
        this.tabPane.setOnDragDropped(event -> {
            if (event.getDragboard().hasFiles()) {
                event.getDragboard().getFiles().forEach(file -> {
                    try {
                        load(file.getName(), file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        //if all tabs is closed the place holder will appears
        DocumentViewerPlaceHolderGUI placeHolder = new DocumentViewerPlaceHolderGUI();
        BooleanBinding isEmptyProperty = Bindings.isEmpty(tabPane.getTabs());
        placeHolder.visibleProperty().bind(isEmptyProperty);

        this.getChildren().add(placeHolder);
    }

    /**
     * Creates a tab on the tab pane
     *
     * @param title                    the title of the tab; can be null
     * @param titlePlaceHolderSupplier the supplier that returns a title that will be
     *                                 displayed if the title parameter is null; mustn't be null
     * @param loader                   the loader that defines how to load to the {@link DocumentRenderer};
     *                                 mustn't be null
     * @param contextMenu              the contextmenu for the tab; can be null
     * @throws IOException if some I/O exception occurs during the loading of the content
     */
    private void createTab(String title, Supplier<String> titlePlaceHolderSupplier, UndhandledConsumer<DocumentRenderer,
            IOException> loader, ContextMenu contextMenu) throws IOException {

        Objects.requireNonNull(loader, "The loader mustn't be null");
        Objects.requireNonNull(titlePlaceHolderSupplier, "The titlePlaceHolderSupplier mustn't be null");

        Tab tab = new Tab();

        if (isNull(title)) tab.setText(titlePlaceHolderSupplier.get());
        else tab.setText(title);

        DocumentRenderer documentRenderer = Objects.requireNonNull(getDocumentRendererSupplier().get(), "The DocumentRenderer supplier shouldn't return null!");
        loader.accept(documentRenderer);

        tab.setContent(documentRenderer.toNode());
        tab.setContextMenu(contextMenu);

        this.tabPane.getTabs().add(tab);
    }

    @Override
    public void load(String title, String url) throws IOException {
        MenuItem openInDefaultViewerItem = new MenuItem(getGeneralValue("document.viewer.tab.open.default"));
        openInDefaultViewerItem.setOnAction(e -> {
            if (SystemBrowser.isSupported())
                new SystemBrowser().browse(url);
        });

        this.createTab(title, () -> url, renderer -> renderer.load(url), new ContextMenu(openInDefaultViewerItem));
    }

    @Override
    public void load(String title, InputStream inputStream) throws IOException {
        this.createTab(title, () -> getGeneralValue("document.viewer.tab.untitled"), renderer -> renderer.load(inputStream), null);
    }

    @Override
    public void load(String title, File file) throws IOException {
        MenuItem openInDefaultViewerItem = new MenuItem(getGeneralValue("document.viewer.tab.open.default"));
        openInDefaultViewerItem.setOnAction(e -> {
            CaughtRunnable runnable = () -> {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            };
            runnable.run();
        });

        this.createTab(title, file::getName, renderer -> renderer.load(file), new ContextMenu(openInDefaultViewerItem));
    }

    @Override
    public void load(String title, URL url) throws IOException {
        this.createTab(title, url::toString, renderer -> renderer.load(url), null);
    }


    @FunctionalInterface
    private interface UndhandledConsumer<T, E extends Throwable> {
        void accept(T t) throws E;
    }
}
