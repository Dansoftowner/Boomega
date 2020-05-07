package com.dansoftware.libraryapp.gui.browser;

import javafx.scene.Node;

import java.util.function.Consumer;

/**
 * A WebContentRenderer is a GUI-capable element
 * that can actually display the web-contents inside
 * a javafx Node.
 *
 * @see Node
 */
public interface WebContentRenderer {

    /**
     * Instructs the renderer to navigate to the previous page
     */
    void goToPreviousPage();

    /**
     * Instructs the renderer to navigate to the forward page
     */
    void goToForwardPage();

    /**
     * Reloads the renderer
     */
    void reload();

    /**
     * Loads the page with the specified url
     *
     * @param url the url to load from
     */
    void load(String url);

    /**
     * Stores the Consumer that will be notified with
     * the location of the web-content when
     * it's changed.
     *
     * @param locationConsumer the consumer to notify; mustn't be null
     * @throws NullPointerException if the locationConsumer is null
     */
    void onLocationChanged(Consumer<String> locationConsumer);

    /**
     * Stores the Consumer that will be notified with
     * the title of the web-content when
     * it's changed.
     *
     * @param titleConsumer the consumer to notify; mustn't be null
     * @throws NullPointerException if the titleConsumer is null
     */
    void onTitleChanged(Consumer<String> titleConsumer);

    /**
     * Returns the current location of the web-content that is displayed.
     *
     * @return the location of the web-content
     */
    String getLocation();

    /**
     * Returns the actual GUI view of the WebContentRenderer.
     *
     * @return the node that makes the web-content visible on the gui
     */
    Node toNode();
}
