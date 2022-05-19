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

package com.dansoftware.boomega.process

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.di.DIService.get
import kotlin.system.exitProcess

class RealtimeSingletonProcessService : SocketBasedSingletonProcessService() {
    override val port: Int get() = get(Preferences::class)[PORT_KEY]

    override fun serializeArguments(args: Array<String>): String {
        return args.joinToString(separator = "|")
    }

    override fun deserializeMessage(message: String): Array<String> {
        return message.split("|").toTypedArray()
    }

    override fun persistPort(port: Int) {
        get(Preferences::class).editor.put(PORT_KEY, port).tryCommit()
    }

    override fun handleRequest(args: Array<String>) {
        ActivityLauncherImpl(args).launch()
    }

    override fun terminate() {
        exitProcess(0)
    }

    companion object {
        private val PORT_KEY = PreferenceKey("singletonProcessServerPort", Int::class.java) { 0 }
    }

}