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

package com.dansoftware.boomega.database.tracking;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.gui.dbmanager.DatabaseTracker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DatabaseTrackerTest {

    private DatabaseTracker databaseTracker;

    @BeforeEach
    void init() {
        databaseTracker = new DatabaseTracker(Preferences.empty());
    }

    @Test
    void itShouldAddDatabase() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);

        // when
        databaseTracker.saveDatabase(database);

        // then
        assertThat(databaseTracker.isDatabaseSaved(database)).isTrue();
    }

    @Test
    void itShouldRemoveDatabase() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);

        // when
        databaseTracker.saveDatabase(database);
        databaseTracker.removeDatabase(database);

        // then
        assertThat(databaseTracker.isDatabaseSaved(database)).isFalse();
    }

    @Test
    void itShouldRegisterUsedDatabase() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);

        // when
        databaseTracker.registerUsedDatabase(database);

        // then
        assertThat(databaseTracker.isDatabaseUsed(database)).isTrue();
    }

    @Test
    void itShouldRegisterClosedDatabase() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);

        // when
        databaseTracker.registerUsedDatabase(database);
        databaseTracker.registerClosedDatabase(database);

        // then
        assertThat(databaseTracker.isDatabaseUsed(database)).isFalse();
    }

    @Test
    void itShouldRegisterObserver() {
        // given
        DatabaseTracker.Observer observer = mock(DatabaseTracker.Observer.class);

        // when
        databaseTracker.registerObserver(observer);

        // then
        assertThat(databaseTracker.hasObserver(observer)).isTrue();
    }

    @Test
    void itShouldRemoveObserver() {
        // given
        DatabaseTracker.Observer observer = mock(DatabaseTracker.Observer.class);

        // when
        databaseTracker.registerObserver(observer);
        databaseTracker.unregisterObserver(observer);

        // then
        assertThat(databaseTracker.hasObserver(observer)).isFalse();
    }

    @Test
    void itShouldNotifyOnSave() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);
        DatabaseObserver observer = new DatabaseObserver();

        // when
        databaseTracker.registerObserver(observer);
        databaseTracker.saveDatabase(database);

        // then
        assertThat(observer.registeredDatabases.contains(database)).isTrue();
    }

    @Test
    void itShouldNotifyOnRemove() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);
        DatabaseObserver observer = new DatabaseObserver();

        // when
        databaseTracker.registerObserver(observer);
        databaseTracker.saveDatabase(database);
        databaseTracker.removeDatabase(database);

        // then
        assertThat(observer.registeredDatabases.contains(database)).isFalse();
    }

    @Test
    void itShouldNotifyOnUse() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);
        DatabaseObserver observer = new DatabaseObserver();

        // when
        databaseTracker.registerObserver(observer);
        databaseTracker.registerUsedDatabase(database);

        // then
        assertThat(observer.usedDatabases.contains(database)).isTrue();
    }

    @Test
    void itShouldNotifyOnClose() {
        // given
        DatabaseMeta database = mock(DatabaseMeta.class);
        DatabaseObserver observer = new DatabaseObserver();

        // when
        databaseTracker.registerObserver(observer);
        databaseTracker.registerUsedDatabase(database);
        databaseTracker.registerClosedDatabase(database);

        // then
        assertThat(observer.usedDatabases.contains(database)).isFalse();
    }

    private static final class DatabaseObserver implements DatabaseTracker.Observer {

        private final List<DatabaseMeta> registeredDatabases = new ArrayList<>();
        private final List<DatabaseMeta> usedDatabases = new ArrayList<>();

        @Override
        public void onUsingDatabase(@NotNull DatabaseMeta databaseMeta) {
            usedDatabases.add(databaseMeta);
        }

        @Override
        public void onClosingDatabase(@NotNull DatabaseMeta databaseMeta) {
            usedDatabases.remove(databaseMeta);
        }

        @Override
        public void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) {
            registeredDatabases.add(databaseMeta);
        }

        @Override
        public void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
            registeredDatabases.remove(databaseMeta);
        }
    }

}
