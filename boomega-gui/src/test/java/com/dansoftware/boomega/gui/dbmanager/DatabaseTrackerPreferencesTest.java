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

package com.dansoftware.boomega.gui.dbmanager;

import com.dansoftware.boomega.config.DummyConfigSource;
import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.di.DIService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import static com.dansoftware.boomega.gui.login.config.GetLoginDataConfigKt.LOGIN_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DatabaseTrackerPreferencesTest {

    private DatabaseTracker underTest;
    private Preferences preferences;

    @BeforeAll
    static void setup() {
        DIService.initModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ExecutorService.class).annotatedWith(Names.named("singleThreadExecutor"))
                        .toInstance(MoreExecutors.newDirectExecutorService());
                bind(ConfigSource.class).to(DummyConfigSource.class);
            }
        });
    }

    @BeforeEach
    void initialize() {
        underTest = DIService.get(DatabaseTracker.class);
        preferences = DIService.get(Preferences.class);
    }

    @Test
    @Order(1)
    void itShouldAddDatabaseToLoginData() {
        var dbMeta = mock(DatabaseMeta.class);
        underTest.saveDatabase(dbMeta);
        assertThat(preferences.get(LOGIN_DATA).getSavedDatabases()).contains(dbMeta);
    }

    @Test
    @Order(2)
    void itShouldRemoveDatabaseFromLoginData() {
        var dbMeta = mock(DatabaseMeta.class);
        underTest.saveDatabase(dbMeta);
        underTest.removeDatabase(dbMeta);
        assertThat(preferences.get(LOGIN_DATA).getSavedDatabases()).doesNotContain(dbMeta);
    }
}
