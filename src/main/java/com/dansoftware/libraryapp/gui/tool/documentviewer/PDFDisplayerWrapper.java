package com.dansoftware.libraryapp.gui.tool.documentviewer;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

public class PDFDisplayerWrapper implements DocumentRenderer {

    public static final Supplier<DocumentRenderer> PDF_DISPLAYER_SUPPLIER = () -> new PDFDisplayerWrapper(new PDFDisplayer());

    private final PDFDisplayer pdfDisplayer;

    private PDFDisplayerWrapper(PDFDisplayer pdfDisplayer) {
        this.pdfDisplayer = Objects.requireNonNull(pdfDisplayer, "The pdfDisplayer mustn't be null");
    }

    @Override
    public void load(String url) throws IOException {
        this.load(new URL(url));
    }

    @Override
    public void load(InputStream inputStream) throws IOException {
        pdfDisplayer.displayPdf(inputStream);
    }

    @Override
    public void load(File file) throws IOException {
        pdfDisplayer.displayPdf(file);
    }

    @Override
    public void load(URL url) throws IOException {
        pdfDisplayer.displayPdf(url);
    }

    @Override
    public Node toNode() {
        return pdfDisplayer.toNode();
    }
}
