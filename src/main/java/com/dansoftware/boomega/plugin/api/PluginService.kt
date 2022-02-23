/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.plugin.api

import java.net.URL

/**
 * Gateway for loading & accessing plugins
 */
interface PluginService {

    /**
     * A read-only list of all the loaded plugin objects.
     */
    val all: List<BoomegaPlugin>

    /**
     * The count of the loaded plugin files.
     */
    val pluginFileCount: Int

    /**
     * Loads the plugins into the memory.
     */
    fun load()

    /**
     * Searchers plugins with the given type.
     *
     * @param type the class-reference of the type
     * @return the list of plugin objects
     */
    fun <P : BoomegaPlugin> of(type: Class<P>): List<P>

    /**
     * Searches plugins with the given location
     */
    fun pluginsOfLocation(url: URL): List<BoomegaPlugin>
}