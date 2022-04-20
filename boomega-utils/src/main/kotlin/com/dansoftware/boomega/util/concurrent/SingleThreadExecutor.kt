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

package com.dansoftware.boomega.util.concurrent

import java.util.concurrent.*
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Named("singleThreadExecutor")
class SingleThreadExecutor : ExecutorService {

    private val back = Executors.newSingleThreadExecutor { runnable -> Thread(runnable).also { it.isDaemon = true } }

    override fun execute(command: Runnable) = back.execute(command)

    override fun shutdown() = back.shutdown()

    override fun shutdownNow(): MutableList<Runnable> = back.shutdownNow()

    override fun isShutdown(): Boolean = back.isShutdown

    override fun isTerminated(): Boolean = back.isTerminated

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = back.awaitTermination(timeout, unit)

    override fun <T : Any?> submit(task: Callable<T>): Future<T> = back.submit(task)

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> = back.submit(task, result)

    override fun submit(task: Runnable): Future<*> = back.submit(task)

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> =
        back.invokeAll(tasks)

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> {
        return back.invokeAll(tasks, timeout, unit)
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T = back.invokeAny(tasks)

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T =
        back.invokeAny(tasks, timeout, unit)
}