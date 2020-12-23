package com.dansoftware.libraryapp.util

import java.util.concurrent.*

object SingleThreadExecutor : ExecutorService {

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