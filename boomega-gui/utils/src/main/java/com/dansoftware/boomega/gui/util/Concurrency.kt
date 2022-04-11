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

@file:JvmName("ConcurrencyUtils")

package com.dansoftware.boomega.gui.util

import com.dansoftware.boomega.gui.api.Context
import javafx.application.Platform
import javafx.concurrent.Service
import javafx.concurrent.Task
import java.util.concurrent.ExecutorService

fun runOnUiThread(action: Runnable) {
    when {
        Platform.isFxApplicationThread() -> action.run()
        else -> Platform.runLater(action)
    }
}

/**
 * Submits a common UI task to the executor-service.
 * It shows indeterminate progress on the given [Context] while the task is running.
 */
inline fun ExecutorService.submitFXTask(context: Context, crossinline callFunction: () -> Unit) {
    submit(object : Task<Unit>() {
        init {
            this.setOnRunning { context.showIndeterminateProgress() }
            this.setOnFailed { context.stopProgress() }
            this.setOnSucceeded { context.stopProgress() }
        }

        override fun call() {
            callFunction()
        }
    })
}

inline fun <T> Task<T>.onSucceeded(crossinline action: (T) -> Unit) {
    setOnSucceeded { action(value) }
}

inline fun <T> Task<T>.onFailed(crossinline action: (Throwable) -> Unit) {
    setOnFailed { action(it.source.exception) }
}

inline fun <T> Task<T>.onRunning(crossinline action: () -> Unit) {
    setOnRunning { action() }
}

inline fun <T> Service<T>.onSucceeded(crossinline action: (T) -> Unit) {
    setOnSucceeded { action(value) }
}

inline fun <T> Service<T>.onFailed(crossinline action: (Throwable) -> Unit) {
    setOnFailed { action(it.source.exception) }
}

inline fun <T> Service<T>.onRunning(crossinline action: () -> Unit) {
    setOnRunning { action() }
}

inline fun <T> Service<T>.onCancelled(crossinline action: () -> Unit) {
    setOnCancelled { action() }
}