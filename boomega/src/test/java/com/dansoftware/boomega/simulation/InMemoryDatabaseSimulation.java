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

package com.dansoftware.boomega.simulation;

import com.dansoftware.boomega.config.DummyConfigSource;
import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.database.api.*;
import com.dansoftware.boomega.database.api.data.Record;
import com.dansoftware.boomega.di.DIService;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.databaseview.DatabaseActivity;
import com.dansoftware.boomega.main.bindings.ConcurrencyModule;
import com.dansoftware.boomega.plugin.api.PluginService;
import com.dansoftware.boomega.simulation.util.DummyPluginService;
import com.google.inject.AbstractModule;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryDatabaseSimulation {

    static {
        DIService.initModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ConfigSource.class).to(DummyConfigSource.class);
                bind(PluginService.class).to(DummyPluginService.class);
            }
        }, new ConcurrencyModule());
    }

    public static void main(String[] args) {
        Application.launch(AppImpl.class);
    }

    public static class AppImpl extends Application {
        @Override
        public void start(Stage primaryStage) {
            new DatabaseActivity(new InMemoryDatabase()).show();
        }
    }

    private static class InMemoryDatabaseProvider implements DatabaseProvider<InMemoryDatabaseMeta> {

        @NotNull
        @Override
        public String getName() {
            return "null";
        }

        @NotNull
        @Override
        public Node getIcon() {
            return new Rectangle();
        }

        @NotNull
        @Override
        public List<DatabaseOption<?>> getAvailableOptions() {
            return List.of();
        }

        @NotNull
        @Override
        public List<DatabaseField<?>> getFields() {
            return List.of();
        }

        @NotNull
        @Override
        public InMemoryDatabaseMeta getMeta(@NotNull String identifier) {
            return new InMemoryDatabaseMeta();
        }

        @NotNull
        @Override
        public Database getDatabase(@NotNull InMemoryDatabaseSimulation.InMemoryDatabaseMeta meta, @NotNull Map<DatabaseField<?>, ?> credentials, @NotNull Map<DatabaseOption<?>, ?> options) throws DatabaseConstructionException {
            return new InMemoryDatabase();
        }

        @NotNull
        @Override
        public LoginForm<InMemoryDatabaseMeta> buildUILoginForm(@NotNull Context context, @NotNull ReadOnlyObjectProperty<InMemoryDatabaseMeta> databaseMeta, @NotNull Map<DatabaseOption<?>, ?> options) {
            return null;
        }

        @NotNull
        @Override
        public RegistrationForm<InMemoryDatabaseMeta> buildUIRegistrationForm(@NotNull Context context, @NotNull Map<DatabaseOption<?>, ?> options) {
            return null;
        }
    }

    private static class InMemoryDatabaseMeta extends DatabaseMeta {


        @NotNull
        @Override
        public String getIdentifier() {
            return "null";
        }

        @NotNull
        @Override
        public String getName() {
            return "null";
        }

        @NotNull
        @Override
        protected Set<Action<?>> getSupportedActions() {
            return Set.of();
        }

        public Object performAction(DatabaseMeta.Action action) {
            return null;
        }

        @NotNull
        @Override
        public DatabaseProvider getProvider() {
            return new InMemoryDatabaseProvider();
        }

        @NotNull
        @Override
        public String getUri() {
            return "null";
        }
    }

    private static class InMemoryDatabase implements Database {

        private final List<Record> records = new LinkedList<>();

        @NotNull
        @Override
        public DatabaseMeta getMeta() {
            return new InMemoryDatabaseMeta();
        }

        @NotNull
        @Override
        public List<Record> getRecords() {
            return List.copyOf(records);
        }

        @Override
        public int getTotalRecordCount() {
            return records.size();
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void insertRecord(@NotNull Record record) {
            records.add(record);
        }

        @Override
        public void updateRecord(@NotNull Record record) {

        }

        @Override
        public void removeRecord(@NotNull Record record) {

        }

        @Override
        public void removeRecords(@NotNull List<Record> records) {

        }

        @Override
        public void close() {

        }

        @Override
        public void addListener(@NotNull DatabaseChangeListener listener) {

        }

        @Override
        public void removeListener(@NotNull DatabaseChangeListener listener) {

        }
    }
}
