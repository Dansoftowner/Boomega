package com.dansoftware.libraryapp.gui.tool.documentviewer;

import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A DocumentRenderer can load and display (PDF) documents
 * on the GUI.
 */
public interface DocumentRenderer {

    /**
     * Loads the document from the given url.
     *
     * @param url the url to load from
     * @throws IOException if some I/O exception occurs during the execution
     */
    void load(String url) throws IOException;

    /**
     * Loads the document from the given inputStream.
     *
     * @param inputStream the input stream to load from
     * @throws IOException if some I/O exception occurs during the execution
     */
    void load(InputStream inputStream) throws IOException;

    /**
     * Loads the document from the given file.
     *
     * @param file the file to load from
     * @throws IOException if some I/O exception occurs during the execution
     */
    void load(File file) throws IOException;

    /**
     * Loads the document from the given url object.
     *
     * @param url the url to load from
     * @throws IOException if some I/O exception occurs during the execution
     */
    void load(URL url) throws IOException;

    /**
     * Gives access to the GUI element of the
     * document renderer.
     *
     * @return the GUI element that displays the document
     */
    Node toNode();
}
