/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.simulation.util

import com.dansoftware.boomega.plugin.api.BoomegaPlugin
import com.dansoftware.boomega.plugin.api.PluginService
import java.net.URL

class DummyPluginService : PluginService {

    override val all: List<BoomegaPlugin>
        get() = emptyList()

    override val pluginFileCount: Int
        get() = 0

    override fun load() { }

    override fun close() { }

    override fun <P : BoomegaPlugin> of(type: Class<P>): List<P> = emptyList()

    override fun pluginsOfLocation(url: URL): List<BoomegaPlugin> = emptyList()
}