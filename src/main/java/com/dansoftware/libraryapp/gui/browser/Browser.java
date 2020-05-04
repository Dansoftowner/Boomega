package com.dansoftware.libraryapp.gui.browser;

import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;

/**
 * A Browser is a GUI element that can load and show
 * web pages inside the application.
 */
public abstract class Browser extends StackPane {

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
        Objects.requireNonNull(url, "The url can't be null"::toString);
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
        Objects.requireNonNull(url, "The url can't be null"::toString);
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
        Objects.requireNonNull(url, "The url can't be null"::toString);
        this.load(null, url);
    }
}
