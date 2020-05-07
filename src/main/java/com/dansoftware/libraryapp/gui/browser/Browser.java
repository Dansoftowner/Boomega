package com.dansoftware.libraryapp.gui.browser;

import javafx.scene.layout.StackPane;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A Browser is a GUI element that can load and show
 * web pages inside the application.
 *
 * <p>
 *  A Browser provides a GUI toolset to give the ability to the user
 *  to do operations with the web page (such as reloading, url copying etc..)
 */
public abstract class Browser extends StackPane {

    private Supplier<WebContentRenderer> rendererSupplier;

    /**
     * Creates a Browser with a Supplier of WebContentRenderer
     *
     * <p>
     * The supplier shouldn't return null and should return a new renderer on every single call.
     *
     * @param rendererSupplier the supplier to get the right web content renderer; mustn't be null
     * @throws NullPointerException if the rendererSupplier is null
     */
    public Browser(Supplier<WebContentRenderer> rendererSupplier) {
        this.rendererSupplier = Objects.requireNonNull(rendererSupplier, "The rendererSupplier mustn't be null");
        this.getStyleClass().add("browser");
    }

    /**
     * Loads the wep page with the specified url and sets a fixed title.
     *
     * @param title a custom title which will not change;
     *              can be null
     * @param url the url to load from; mustn't be null
     * @throws NullPointerException if the url is null
     */
    public abstract void load(String title, String url);

    /**
     * Loads the wep page with the specified URL object and sets a fixed title.
     *
     * <p>
     *  This method actually calls the {@link Browser#load(String, String)} method
     *  with the {@link URL#toExternalForm()} for the second parameter.
     *
     * @param title a custom title which will not change;
     *              can be null
     * @param url the url to load from
     * @throws NullPointerException if the url is null
     */
    public void load(String title, URL url) {
        Objects.requireNonNull(url, "The url can't be null");
        this.load(title, url.toExternalForm());
    }

    /**
     * Loads the web page without a predefined, fixed title.
     *
     * <p>
     *  This method actually calls the {@link Browser#load(String, String)} method
     *  with null value for the first parameter
     *
     * @param url the url to load from; mustn't be null
     * @throws NullPointerException if the url is null
     */
    public void load(String url) {
        this.load(null, url);
    }

    /**
     * Loads the web page with the specified URL object without a predefined, fixed title;
     *
     * <p>
     * This method actually calls the {@link Browser#load(String, URL)}
     * with null value for the first parameter
     *
     * @param url the url to load from; mustn't be null
     * @throws NullPointerException if the url is null
     */
    public void load(URL url) {
        this.load(null, url);
    }

    public void load(String title, File file) throws MalformedURLException {
        Objects.requireNonNull(file, "The file mustn't be null");
        this.load(title, file.toURI().toURL());
    }

    public void load(File file) throws MalformedURLException {
        this.load(null, file);
    }

    protected Supplier<WebContentRenderer> getWebRendererSupplier() {
        return this.rendererSupplier;
    }
}
