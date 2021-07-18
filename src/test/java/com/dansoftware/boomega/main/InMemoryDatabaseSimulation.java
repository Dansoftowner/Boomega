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

package com.dansoftware.boomega.main;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.config.source.InMemorySource;
import com.dansoftware.boomega.db.InMemoryDatabase;
import com.dansoftware.boomega.gui.databaseview.DatabaseActivity;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.util.ReflectionUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class InMemoryDatabaseSimulation extends Application {

    static {
        try {
            ReflectionUtils.forName(Main.class);
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new DatabaseActivity(new InMemoryDatabase(), Preferences.empty(), new DatabaseTracker()).show();
    }
}
