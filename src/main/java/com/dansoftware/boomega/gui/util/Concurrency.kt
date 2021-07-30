/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

@file:JvmName("ConcurrencyUtils")

package com.dansoftware.boomega.gui.util

import javafx.application.Platform
import javafx.concurrent.Task

fun runOnUiThread(action: Runnable) {
    when {
        Platform.isFxApplicationThread() -> action.run()
        else -> Platform.runLater(action)
    }
}

fun <T> Task<T>.onSucceeded(action: (T) -> Unit) {
    setOnSucceeded { action(value) }
}

fun <T> Task<T>.onFailed(action: (Throwable) -> Unit) {
    setOnFailed { action(it.source.exception) }
}

fun <T> Task<T>.onRunning(action: () -> Unit) {
    setOnRunning { action() }
}