package com.dansoftware.boomega.plugin.api;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base plugin class.
 *
 * @author Daniel Gyoerffy
 */
public interface BoomegaPlugin {

    /**
     * Executed when the plugin object is created by the application
     */
    void init();

    /**
     * Executed when the user removes the plugin
     */
    void destroy(); //TODO: use it

    /**
     * @return the icon for the plugin
     */
    @Nullable
    Image getIcon();

    /**
     * @return the plugin's name
     */
    @NotNull
    String getName();

    /**
     * @return the Plugin version
     */
    @NotNull
    String getVersion();

    /**
     * @return the Plugin description
     */
    @Nullable
    String getDescription();

    /**
     * @return the plugin's author's information object
     */
    @NotNull
    PluginAuthor getAuthor();
}
