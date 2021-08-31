package com.dansoftware.boomega.plugin.api

import javafx.scene.image.Image

/**
 * The base plugin interface.
 */
interface BoomegaPlugin {

    /**
     * The plugin's name
     */
    val name: String

    /**
     * @return the plugin's author's information object
     */
    val author: PluginAuthor

    /**
     * The plugin's version
     */
    val version: String

    /**
     * The plugin's description
     */
    val description: String?

    /**
     * The icon for the plugin
     */
    val icon: Image?

    /**
     * Executed when the plugin object is created by the application
     */
    fun init()

    /**
     * Executed when the user removes the plugin
     */
    fun destroy() //TODO: use it
}