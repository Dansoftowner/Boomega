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
package com.dansoftware.boomega.gui.dbmanager

import com.dansoftware.boomega.config.LOGIN_DATA
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.gui.login.updateLoginData
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import java.util.*
import java.util.function.Consumer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Used for tracking the opened/unopened databases (in form of [DatabaseMeta] objects).
 *
 * Some components might use [DatabaseTracker]s for updating their content when something is changed
 * (a database is launched, removed etc...).
 *
 * This can be achieved by registering [DatabaseTracker.Observer]s.
 *
 * @param preferences the preferences that should be synchronized with the database tracker.
 */
@Singleton
class DatabaseTracker @Inject constructor(preferences: Preferences) {

    private val observers = Collections.synchronizedList(LinkedList<WeakReference<Observer>>())
    private val strongObservers = Collections.synchronizedSet(HashSet<Observer>())

    // TODO: use property delegation implementing this

    private val savedDatabasesImpl = FXCollections.synchronizedObservableSet(FXCollections.observableSet<DatabaseMeta>())
    val savedDatabases: ObservableSet<DatabaseMeta> = FXCollections.unmodifiableObservableSet(savedDatabasesImpl)

    private val usingDatabasesImpl = FXCollections.synchronizedObservableSet(FXCollections.observableSet<DatabaseMeta>())
    val usingDatabases: ObservableSet<DatabaseMeta> = FXCollections.unmodifiableObservableSet(usingDatabasesImpl)

    init {
        preferences[LOGIN_DATA].savedDatabases.forEach(::saveDatabase)
        initPreferencesHandling(preferences)
    }

    private fun initPreferencesHandling(preferences: Preferences) {
        registerObserverStrongly(object : Observer {
            override fun onDatabaseAdded(databaseMeta: DatabaseMeta) {
                preferences.updateLoginData { it.savedDatabases.add(databaseMeta) }
            }

            override fun onDatabaseRemoved(databaseMeta: DatabaseMeta) {
                preferences.updateLoginData { it.savedDatabases.remove(databaseMeta) }
            }
        })
    }

    /**
     * Marks the database as closed.
     *
     * @param databaseMeta the meta database
     */
    fun registerClosedDatabase(databaseMeta: DatabaseMeta): Boolean {
        logger.debug("Registering database as closed: '{}'", databaseMeta.identifier)
        val removed = usingDatabasesImpl.remove(databaseMeta)
        if (removed) iterateObservers { it!!.onClosingDatabase(databaseMeta) }
        return removed
    }

    /**
     * Marks the database as used.
     *
     * @param databaseMeta the meta database
     */
    fun registerUsedDatabase(databaseMeta: DatabaseMeta): Boolean {
        logger.debug("Registering database as used: '{}'", databaseMeta.identifier)
        val inserted = usingDatabasesImpl.add(databaseMeta)
        if (inserted) iterateObservers { it!!.onUsingDatabase(databaseMeta) }
        return inserted
    }

    /**
     * Registers a database.
     *
     * @param databaseMeta the meta database
     */
    fun saveDatabase(databaseMeta: DatabaseMeta): Boolean {
        logger.debug("Registering database: '{}'", databaseMeta.identifier)
        val inserted = savedDatabasesImpl.add(databaseMeta)
        if (inserted) iterateObservers { observer: Observer? -> observer!!.onDatabaseAdded(databaseMeta) }
        return inserted
    }

    /**
     * Unregisters a database.
     *
     * @param databaseMeta the meta database
     */
    fun removeDatabase(databaseMeta: DatabaseMeta): Boolean {
        logger.debug("Unregistering database: '{}'", databaseMeta.identifier)
        val removed = savedDatabasesImpl.remove(databaseMeta)
        if (removed) logger.debug("Removed from DatabaseTracker '{}'", databaseMeta)
        else logger.debug("DatabaseMeta '{}' not found in savedDatabases", databaseMeta)
        iterateObservers { it!!.onDatabaseRemoved(databaseMeta) }
        return removed
    }

    /**
     * Registers an [Observer] that will be notified on every change.
     *
     * Note: the observer will be registered as a **weak** reference, so if there is no
     * other reference that points to the observer object, the observer will be removed by the GC
     *
     * @param observer the observer object; shouldn't be null
     */
    fun registerObserver(observer: Observer) {
        if (findWeakReference(observer).isEmpty) observers.add(WeakReference(observer))
    }

    /**
     * Registers an [Observer] that will be notified on every change.
     *
     * Note: unlike the [registerObserver] method, it registers the observer strongly in memory
     * meaning that no other reference required to keep the observer alive.
     */
    fun registerObserverStrongly(observer: Observer) {
        Objects.requireNonNull(observer)
        strongObservers.add(observer)
        registerObserver(observer)
    }

    /**
     * Unregisters an [Observer]
     *
     * @param observer the observer object to be removed
     */
    fun unregisterObserver(observer: Observer?) {
        if (observer != null) findWeakReference(observer).ifPresent(observers::remove)
    }

    fun hasObserver(observer: Observer?): Boolean {
        return findWeakReference(observer).isPresent
    }

    private fun findWeakReference(observer: Observer?): Optional<WeakReference<Observer>> {
        return observers.stream()
            .filter { ref: WeakReference<Observer> -> ref.get() === observer }
            .findAny()
    }

    private fun iterateObservers(observerConsumer: Consumer<Observer?>) {
        val safeConsumer = { it: Observer? ->
            try {
                observerConsumer.accept(it)
            } catch (e: Exception) {
                logger.error("exception caught from observer", e)
            }
        }
        observers.mapNotNull { it.get() }.forEach(safeConsumer)
    }

    fun isDatabaseSaved(databaseMeta: DatabaseMeta?): Boolean {
        return savedDatabasesImpl.contains(databaseMeta)
    }

    fun isDatabaseNotSaved(databaseMeta: DatabaseMeta?): Boolean {
        return !isDatabaseSaved(databaseMeta)
    }

    fun isDatabaseClosed(databaseMeta: DatabaseMeta?): Boolean {
        return !isDatabaseUsed(databaseMeta)
    }

    fun isDatabaseUsed(databaseMeta: DatabaseMeta?): Boolean {
        return usingDatabasesImpl.contains(databaseMeta)
    }

    interface Observer {
        fun onUsingDatabase(databaseMeta: DatabaseMeta) {}
        fun onClosingDatabase(databaseMeta: DatabaseMeta) {}
        fun onDatabaseAdded(databaseMeta: DatabaseMeta) {}
        fun onDatabaseRemoved(databaseMeta: DatabaseMeta) {}
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseTracker::class.java)
    }
}