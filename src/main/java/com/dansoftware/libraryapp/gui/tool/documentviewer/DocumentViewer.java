package com.dansoftware.libraryapp.gui.tool.documentviewer;

import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A DocumentViewer is a GUI object that can display document(s) with some
 * additional features (for example multiple tabs)
 *
 * <p>
 * The actual pdf rendering is the job of a particular {@link DocumentRenderer}
 *
 * @see DocumentRenderer
 */
public abstract class DocumentViewer extends StackPane {

    private final Supplier<DocumentRenderer> rendererSupplier;

    public DocumentViewer(Supplier<DocumentRenderer> rendererSupplier) {
        this.rendererSupplier = Objects.requireNonNull(rendererSupplier, "The rendererSupplier mustn't be null");
        this.getStyleClass().add("document-viewer");
    }

    public abstract void load(String title, String url) throws IOException;

    public abstract void load(String title, InputStream inputStream) throws IOException;

    public abstract void load(String title, File file) throws IOException;

    public abstract void load(String title, URL url) throws IOException;

    public void load(File file) throws IOException {
        this.load(null, file);
    }

    protected Supplier<DocumentRenderer> getDocumentRendererSupplier() {
        return rendererSupplier;
    }
}
