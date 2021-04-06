package com.dansoftware.boomega.plugin.api

/**
 * Represents the author of a plugin.
 */
data class PluginAuthor(val name: String, val email: String?, val description: String? = null)